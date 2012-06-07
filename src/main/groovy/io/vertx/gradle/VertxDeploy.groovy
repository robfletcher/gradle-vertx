package io.vertx.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import org.vertx.java.deploy.impl.cli.DeployCommand

class VertxDeploy extends DefaultTask {

	String main
	String verticleName
	boolean worker = false
	int instances = 1

	private final VertxManager vertxManager = VertxManager.instance
	private Collection<File> classpathFiles = [project.projectDir]

	@TaskAction
	def vertxDeploy() {
		println vertxManager.execute(createDeployCommand())
	}

	VertxDeploy setClasspath(FileCollection classpath) {
		classpathFiles = classpath.files
		this
	}

	VertxDeploy classpath(File... paths) {
		classpathFiles = paths.toList()
		this
	}

	private DeployCommand createDeployCommand() {
		new DeployCommand(worker, verticleName, main, null, getClasspathURLs(), instances)
	}

	private URL[] getClasspathURLs() {
		// TODO: this should be configurable based on a classpath
		classpathFiles.collect { it.toURI().toURL() } as URL[]
	}
}
