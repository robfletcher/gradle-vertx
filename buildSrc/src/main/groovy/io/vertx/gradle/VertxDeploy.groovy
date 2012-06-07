package io.vertx.gradle

import org.gradle.api.tasks.TaskAction

class VertxDeploy extends AbstractVertxDeployTask {

	@TaskAction
	def vertxDeploy() {
		println vertxManager.execute(createDeployCommand())
	}

}
