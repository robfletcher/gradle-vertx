package io.vertx.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class VertxStop extends DefaultTask {

	VertxPlugin plugin

	@TaskAction
	def vertxStop() {
		def doneHandler = new BlockingHandler()
		plugin.mgr.undeployAll(doneHandler)
		assert doneHandler.block(), "$name timed out"
	}

}
