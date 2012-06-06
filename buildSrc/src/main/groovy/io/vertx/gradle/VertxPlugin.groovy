package io.vertx.gradle

import org.gradle.api.*
import org.vertx.java.core.Vertx
import org.vertx.java.core.impl.DefaultVertx
import org.vertx.java.deploy.impl.VerticleManager
import org.vertx.java.deploy.impl.Args
import org.vertx.java.deploy.impl.cli.VertxCommand
import org.vertx.java.core.net.NetClient
import org.vertx.java.core.net.NetSocket
import org.vertx.java.core.Handler
import org.vertx.java.core.parsetools.RecordParser
import org.vertx.java.core.buffer.Buffer
import org.vertx.java.core.SimpleHandler
import java.util.concurrent.*
import java.util.concurrent.atomic.*

class VertxPlugin implements Plugin<Project> {

	void apply(Project project) {
		def vertxRun = project.tasks.add('vertxRun', VertxRun)
		vertxRun.plugin = this
		def vertxStop = project.tasks.add('vertxStop', VertxStop)
		vertxStop.plugin = this

		project.task('vertxStart') << {}
		project.task('vertxDeploy') << {}
		project.task('vertxUndeploy') << {}

	}

	private Vertx vertx = new DefaultVertx()
	protected VerticleManager mgr

	boolean startCluster(Args args) {
		boolean clustered = args.map.get("-cluster") != null;
		if (clustered) {
			System.out.print("Starting clustering...");
			int clusterPort = args.getInt("-cluster-port");
			if (clusterPort == -1) {
				clusterPort = 25500;
			}
			String clusterHost = args.map.get("-cluster-host");
			if (clusterHost == null) {
				clusterHost = getDefaultAddress();
				if (clusterHost == null) {
					System.err.println("Unable to find a default network interface for clustering. Please specify one using -cluster-host");
					return false;
				} else {
					System.out.println("No cluster-host specified so using address " + clusterHost);
				}
			}
			vertx = new DefaultVertx(clusterPort, clusterHost);
		}
		mgr = new VerticleManager(vertx);
		if (clustered) {
			System.out.println("Started");
		}
		return true;
	}

	private String getDefaultAddress() {
		Enumeration<NetworkInterface> nets;
		try {
			nets = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			return null;
		}
		NetworkInterface netinf;
		while (nets.hasMoreElements()) {
			netinf = nets.nextElement();

			Enumeration<InetAddress> addresses = netinf.getInetAddresses();

			while (addresses.hasMoreElements()) {
				InetAddress address = addresses.nextElement();
				if (!address.isAnyLocalAddress() && !address.isMulticastAddress()
						&& !(address instanceof Inet6Address)) {
					return address.getHostAddress();
				}
			}
		}
		return null;
	}

	private String sendCommand(final int port, final VertxCommand command) {
		final CountDownLatch latch = new CountDownLatch(1)
		final AtomicReference<String> result = new AtomicReference<>()
		final NetClient client = vertx.createNetClient()
		client.connect(port, "localhost", new Handler<NetSocket>() {
			public void handle(NetSocket socket) {
				if (command.isBlock()) {
					socket.dataHandler(RecordParser.newDelimited("\n", new Handler<Buffer>() {
						public void handle(Buffer buff) {
							result.set(buff.toString());
							client.close();
							latch.countDown();
						}
					}));
					command.write(socket, null);
				} else {
					command.write(socket, new SimpleHandler() {
						public void handle() {
							client.close();
							latch.countDown();
						}
					});
				}
			}
		});

		while (true) {
			try {
				if (!latch.await(10, java.util.concurrent.TimeUnit.SECONDS)) {
					throw new IllegalStateException("Timed out while sending command");
				}
				break;
			} catch (InterruptedException e) {
				//Ignore
			}
		}

		return result.get();
	}
}
