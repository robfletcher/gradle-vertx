package io.vertx.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class VertxUndeploy extends DefaultTask {

	String verticleName

	VertxPlugin plugin

	@TaskAction
	def vertxUndeploy() {
		def doneHandler = new BlockingHandler()
		plugin.mgr.undeploy(verticleName, doneHandler)
		assert doneHandler.block(), "Timed out undeploying $verticleName"
	}

}
