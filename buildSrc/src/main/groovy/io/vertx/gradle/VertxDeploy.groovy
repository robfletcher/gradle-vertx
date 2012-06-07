package io.vertx.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class VertxDeploy extends DefaultTask {

	String main
	String verticleName
	boolean worker = false
	int instances = 1

	VertxPlugin plugin

	@TaskAction
	def vertxDeploy() {
		def jsonConf
		def urls = [project.projectDir.toURI().toURL()] as URL[]

		def doneHandler = new BlockingHandler()
		plugin.mgr.deploy(worker, verticleName, main, jsonConf, urls, instances, null, doneHandler)
		assert doneHandler.block(), "Timed out deploying $main"
	}

}
