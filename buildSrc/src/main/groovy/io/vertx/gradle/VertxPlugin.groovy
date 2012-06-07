package io.vertx.gradle

import org.vertx.java.deploy.impl.VerticleManager
import org.gradle.api.*
import org.vertx.java.core.impl.*

class VertxPlugin implements Plugin<Project> {

	void apply(Project project) {
		def vertxRun = project.tasks.add('vertxRun', VertxRun)
		vertxRun.plugin = this

		def vertxStop = project.tasks.add('vertxStop', VertxStop)
		vertxStop.plugin = this

		def vertxStart = project.tasks.add('vertxStart', VertxStart)
		vertxStart.plugin = this

		def vertxDeploy = project.tasks.add('vertxDeploy', VertxDeploy)
		vertxDeploy.plugin = this

		def vertxUndeploy = project.tasks.add('vertxUndeploy', VertxUndeploy)
		vertxUndeploy.plugin = this
	}

	private VertxInternal vertx = new DefaultVertx()
	protected VerticleManager mgr

	void startVertx() {
		if (!mgr) mgr = new VerticleManager(vertx)
	}
}
