package io.vertx.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.vertx.java.deploy.impl.Args
import org.vertx.java.deploy.impl.cli.DeployCommand
import java.util.concurrent.CountDownLatch
import org.vertx.java.core.Handler

class VertxRun extends DefaultTask {

	String main
//	String name
	int instances = 1
	boolean worker = false

	VertxPlugin plugin

	@TaskAction
	def vertxRun() {
		if (plugin.startCluster(new Args([] as String[]))) {
			def dc = new DeployCommand(worker, null, main, null, [project.projectDir.toURI().toURL()] as URL[], instances)
			def jsonConf = null

			def latch = new CountDownLatch(1)
			plugin.mgr.deploy(dc.worker, dc.name, dc.main, jsonConf, dc.urls, dc.instances, null, new Handler() {
				@Override
				void handle(e) {
					latch.countDown()
				}
			})
			latch.await()
		}
	}

}
