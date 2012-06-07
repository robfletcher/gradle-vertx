package io.vertx.gradle

import org.gradle.api.tasks.TaskAction
import org.gradle.api.DefaultTask

class VertxRun extends DefaultTask {

	String main
	String verticleName
	int instances = 1
	boolean worker = false

	VertxPlugin plugin

	@TaskAction
	def vertxRun() {
		plugin.startVertx()

		def jsonConf
		def urls = [project.projectDir.toURI().toURL()] as URL[]

		def doneHandler = new BlockingHandler()
		plugin.mgr.deploy(worker, verticleName, main, jsonConf, urls, instances, null, doneHandler)
		assert doneHandler.block(), "Timed out deploying $main"
	}

}
