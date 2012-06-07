package io.vertx.gradle

import org.gradle.api.Project
import org.gradle.api.internal.AbstractTask
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.*

class VertxPluginSpec extends Specification {

	static final URL VERTICLE_URL = 'http://localhost:8080/'.toURL()

	Project project = ProjectBuilder.builder().withProjectDir(new File('.')).build()

	AbstractTask vertxStart
	AbstractTask vertxStop
	VertxDeploy vertxDeploy
	VertxUndeploy vertxUndeploy
	AbstractTask vertxRun

	void setup() {
		project.apply plugin: 'vertx'

		vertxStart = project.tasks.vertxStart
		vertxStop = project.tasks.vertxStop
		vertxDeploy = project.tasks.vertxDeploy
		vertxUndeploy = project.tasks.vertxUndeploy
		vertxRun = project.tasks.vertxRun
	}

	void cleanup() {
		VertxManager.instance.stopVertx()
	}

	@Ignore void 'can run a vertx app'() {
		when:
		vertxRun.main = 'src/test/resources/Server.groovy'
		vertxRun.execute()

		then:
		VERTICLE_URL.text == 'O HAI!'
	}

	void 'can stop a vertx app'() {
		given:
		vertxStart.execute()
		vertxDeploy.main = 'src/test/resources/Server.groovy'
		vertxDeploy.execute()

		when:
		vertxStop.execute()

		and:
		VERTICLE_URL.text

		then:
		thrown ConnectException
	}

	void 'can start a vertx container then deploy to it'() {
		given:
		vertxDeploy.main = 'src/test/resources/Server.groovy'

		when:
		vertxStart.execute()
		vertxDeploy.execute()

		then:
		VERTICLE_URL.text == 'O HAI!'
	}

	void 'can undeploy a verticle'() {
		given:
		vertxStart.execute()
		vertxDeploy.main = 'src/test/resources/Server.groovy'
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
