package io.vertx.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.vertx.java.deploy.impl.cli.UndeployCommand

class VertxUndeploy extends DefaultTask {

	String verticleName

	private final VertxManager vertxManager = VertxManager.instance

	@TaskAction
	def vertxUndeploy() {
		vertxManager.execute(new UndeployCommand(verticleName))
	}

}
