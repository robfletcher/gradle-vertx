package io.vertx.gradle

import org.gradle.api.*

class VertxPlugin implements Plugin<Project> {

	void apply(Project project) {
		def vertxStop = project.task('vertxStop') << {
			vertxManager.stopVertx()
		}
		vertxStop.description = 'Stops the vert.x container.'
		vertxStop.group = 'vert.x'

		def vertxStart = project.task('vertxStart') << {
			vertxManager.startVertx()
		}
		vertxStart.description = 'Starts the vert.x container.'
		vertxStart.group = 'vert.x'

		def vertxDeploy = project.tasks.add('vertxDeploy', VertxDeploy)
		vertxDeploy.description = 'Deploys a verticle to the vert.x container.'
		vertxDeploy.group = 'vert.x'
		vertxDeploy.dependsOn('vertxStart')

		def vertxUndeploy = project.tasks.add('vertxUndeploy', VertxUndeploy)
		vertxUndeploy.description = 'Undeploys a verticle from the vert.x container.'
		vertxUndeploy.group = 'vert.x'

		def vertxRun = project.task('vertxRun') << {
			vertxManager.block()
		}
		vertxRun.description = 'Runs a verticle in the vert.x container.'
		vertxRun.group = 'vert.x'
		vertxRun.dependsOn('vertxDeploy')
	}

	private final VertxManager vertxManager = VertxManager.instance

}
