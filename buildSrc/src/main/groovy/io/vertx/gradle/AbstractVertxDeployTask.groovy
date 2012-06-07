package io.vertx.gradle

import org.gradle.api.DefaultTask
import org.vertx.java.deploy.impl.cli.DeployCommand
import org.gradle.api.file.FileCollection

abstract class AbstractVertxDeployTask extends DefaultTask {

	String main
	String verticleName
	boolean worker = false
	int instances = 1

	private Collection<File> classpathFiles = [project.projectDir]

	protected final VertxManager vertxManager = VertxManager.instance

	public AbstractVertxDeployTask setClasspath(FileCollection classpath) {
		classpathFiles = classpath.files
		this
	}

	public AbstractVertxDeployTask classpath(File... paths) {
		classpathFiles = paths.toList()
		this
	}


	protected DeployCommand createDeployCommand() {
		new DeployCommand(worker, verticleName, main, null, getClasspathURLs(), instances)
	}

	private URL[] getClasspathURLs() {
		// TODO: this should be configurable based on a classpath
		println "Using classpath: $classpathFiles"
		classpathFiles.collect { it.toURI().toURL() } as URL[]
//		project.configurations.runtime.collect { File file -> file.toURI().toURL() } as URL[]
	}
}
