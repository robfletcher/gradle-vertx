package io.vertx.gradle

import org.vertx.java.core.buffer.Buffer
import org.vertx.java.core.impl.DefaultVertx
import org.vertx.java.core.parsetools.RecordParser
import org.vertx.java.deploy.impl.VerticleManager

import java.util.concurrent.atomic.AtomicReference

import org.gradle.api.*
import org.vertx.java.core.*
import org.vertx.java.core.net.*
import org.vertx.java.deploy.impl.cli.*

import java.util.concurrent.*
import org.vertx.java.deploy.impl.Args

class VertxPlugin implements Plugin<Project> {

	private Vertx vertx = new DefaultVertx()
	private VerticleManager mgr

	void apply(Project project) {
		project.task('vertxRun') << {
			if (startCluster(new Args([] as String[]))) {
				def main = 'Server.groovy'
				def dc = new DeployCommand(false, null, 'Server.groovy', null, [project.projectDir.toURI().toURL()] as URL[], 1)
				def jsonConf = null

				def latch = new CountDownLatch(1)
				mgr.deploy(dc.worker, dc.name, dc.main, jsonConf, dc.urls, dc.instances, null, new Handler() {
					@Override
					void handle(e) {
						latch.countDown()
					}
				})
				latch.await()
			}
		}
	}

	private boolean startCluster(Args args) {
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
				if (!latch.await(10, TimeUnit.SECONDS)) {
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