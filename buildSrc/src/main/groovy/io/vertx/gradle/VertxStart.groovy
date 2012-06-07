package io.vertx.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.vertx.java.deploy.impl.cli.SocketDeployer

class VertxStart extends DefaultTask {

	int deployPort = -1

	VertxPlugin plugin

	@TaskAction
	def vertxStart() {
		plugin.startVertx()
		SocketDeployer sd = new SocketDeployer(plugin.vertx, plugin.mgr, deployPort);
		sd.start()
	}

}
