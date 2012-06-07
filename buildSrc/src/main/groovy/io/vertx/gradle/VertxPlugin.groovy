package io.vertx.gradle

import org.gradle.api.*

class VertxPlugin implements Plugin<Project> {

	void apply(Project project) {
		project.task('vertxStop') << {
			vertxManager.stopVertx()
		}

		project.task('vertxStart') << {
			vertxManager.startVertx()
		}

		project.tasks.add('vertxDeploy', VertxDeploy)
		project.tasks.add('vertxUndeploy', VertxUndeploy)
		project.tasks.add('vertxRun', VertxRun)
	}

	private final VertxManager vertxManager = VertxManager.instance

}
