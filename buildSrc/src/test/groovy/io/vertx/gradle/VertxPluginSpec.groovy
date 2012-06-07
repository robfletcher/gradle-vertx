package io.vertx.gradle

import static java.util.concurrent.TimeUnit.SECONDS

import org.gradle.api.Project
import org.gradle.api.internal.AbstractTask
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.*

class VertxPluginSpec extends Specification {

	static final URL VERTICLE_URL = 'http://localhost:8080/'.toURL()

	Project project = ProjectBuilder.builder().withProjectDir(new File('/Users/rob/Workspace/gr8conf.2012/gradle-vertx/buildSrc/src/test/resources')).build()

	AbstractTask vertxStart
	AbstractTask vertxStop
	VertxDeploy vertxDeploy
	VertxUndeploy vertxUndeploy
	VertxRun vertxRun

	void setup() {
		project.apply plugin: VertxPlugin

		vertxStart = project.tasks.vertxStart
		vertxStop = project.tasks.vertxStop
		vertxDeploy = project.tasks.vertxDeploy
		vertxUndeploy = project.tasks.vertxUndeploy
		vertxRun = project.tasks.vertxRun
	}

	void cleanup() {
		VertxManager.instance.stopVertx()
	}

	void 'can run a vertx app'() {
		when:
		vertxRun.main = 'Server.groovy'
		vertxRun.execute()

		then:
		VERTICLE_URL.text == 'O HAI!'
	}

	void 'can stop a vertx app'() {
		given:
		vertxRun.main = 'Server.groovy'
		vertxRun.execute()

		when:
		vertxStop.execute()

		and:
		VERTICLE_URL.text

		then:
		thrown ConnectException
	}

	void 'can start a vertx container then deploy to it'() {
		given:
		vertxDeploy.main = 'Server.groovy'

		when:
		vertxStart.execute()
		vertxDeploy.execute()

		then:
		VERTICLE_URL.text == 'O HAI!'
	}

	void 'can undeploy a verticle'() {
		given:
		vertxRun.main = 'Server.groovy'
		vertxRun.verticleName = 'foo'
		vertxRun.execute()

		when:
		vertxUndeploy.verticleName = 'foo'
		vertxUndeploy.execute()

		and:
		VERTICLE_URL.text

		then:
		thrown ConnectException
	}

}
