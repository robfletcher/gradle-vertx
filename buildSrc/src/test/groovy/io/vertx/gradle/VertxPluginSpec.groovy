package io.vertx.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.*

class VertxPluginSpec extends Specification {

	static final URL VERTICLE_URL = 'http://localhost:8080/'.toURL()

	@Shared Project project = ProjectBuilder.builder().withName('test').withProjectDir(new File('/Users/rob/Workspace/gr8conf.2012/gradle-vertx/buildSrc/src/test/resources')).build()

	VertxStart vertxStart
	VertxStop vertxStop
	VertxDeploy vertxDeploy
	VertxUndeploy vertxUndeploy
	VertxRun vertxRun

	void setupSpec() {
		project.apply plugin: VertxPlugin
	}

	void setup() {
		vertxStart = project.tasks.vertxStart
		vertxStop = project.tasks.vertxStop
		vertxDeploy = project.tasks.vertxDeploy
		vertxUndeploy = project.tasks.vertxUndeploy
		vertxRun = project.tasks.vertxRun
	}

	void cleanup() {
		vertxStop.execute()
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

	@Ignore
	void 'can undeploy a verticle'() {
		given:
		vertxStart.execute()
		vertxDeploy.main = 'Server.groovy'
		vertxDeploy.verticleName = 'foo'
		vertxDeploy.execute()

		when:
		vertxUndeploy.verticleName = 'foo'
		vertxUndeploy.execute()

		and:
		VERTICLE_URL.text

		then:
		thrown ConnectException
	}

}
