package io.vertx.gradle

import org.gradle.api.tasks.TaskAction

class VertxRun extends AbstractVertxDeployTask {

	@TaskAction
	def vertxRun() {
		vertxManager.startVertx()
		println vertxManager.execute(createDeployCommand())
	}

}
