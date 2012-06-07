package io.vertx.gradle

import org.gradle.api.DefaultTask
import org.vertx.java.deploy.impl.cli.DeployCommand

abstract class AbstractVertxDeployTask extends DefaultTask {

	String main
	String verticleName
	boolean worker = false
	int instances = 1

	protected final VertxManager vertxManager = VertxManager.instance

	protected DeployCommand createDeployCommand() {
		new DeployCommand(worker, verticleName, main, null, getClasspathURLs(), instances)
	}

	private URL[] getClasspathURLs() {
		// TODO: this should be configurable based on a classpath
		[project.projectDir.toURI().toURL()] as URL[]
	}
}
