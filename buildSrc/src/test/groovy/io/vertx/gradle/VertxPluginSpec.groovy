package io.vertx.gradle

import org.gradle.api.Project
import org.gradle.api.internal.AbstractTask
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.Ignore

class VertxPluginSpec extends Specification {

	static final URL VERTICLE_URL = 'http://localhost:8080/'.toURL()

	Project project = ProjectBuilder.builder().withProjectDir(new File('/Users/rob/Workspace/gr8conf.2012/gradle-vertx/buildSrc/src/test/resources')).build()

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
		vertxRun.main = 'Server.groovy'
		vertxRun.execute()

		then:
		VERTICLE_URL.text == 'O HAI!'
	}

	void 'can stop a vertx app'() {
		given:
		vertxStart.execute()
		vertxDeploy.main = 'Server.groovy'
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
		vertxDeploy.main = 'Server.groovy'

		when:
		vertxStart.execute()
		vertxDeploy.execute()

		then:
		VERTICLE_URL.text == 'O HAI!'
	}

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
