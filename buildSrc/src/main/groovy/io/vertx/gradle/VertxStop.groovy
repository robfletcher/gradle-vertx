package io.vertx.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.vertx.java.core.Handler

import java.util.concurrent.CountDownLatch

class VertxStop extends DefaultTask {

	VertxPlugin plugin

	@TaskAction
	def vertxStop() {
		def latch = new CountDownLatch(1)
		plugin.mgr.undeployAll(new Handler() {
			@Override
			void handle(e) {
				latch.countDown()
			}
		})
		latch.await()
	}

}
