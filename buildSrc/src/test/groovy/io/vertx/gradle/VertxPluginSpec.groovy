package io.vertx.gradle

import spock.lang.Specification
import spock.lang.Stepwise
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import spock.lang.Shared

@Stepwise
class VertxPluginSpec extends Specification {

	@Shared Project project = ProjectBuilder.builder().withProjectDir(new File('/Users/rob/Workspace/gr8conf.2012/gradle-vertx/buildSrc/src/test/resources')).build()

	void setupSpec() {
		project.apply plugin: VertxPlugin
	}

	void 'can start a vertx app'() {
		when:
		project.tasks.vertxRun.execute()

		then:
		HttpURLConnection connection = new URL('http://localhost:8080/').openConnection()
		connection.getResponseCode() == HttpURLConnection.HTTP_OK
		connection.inputStream.text.startsWith('<!doctype html>')
	}

	void 'can stop a vertx app'() {
		expect:
		new URL('http://localhost:8080/').openConnection().getResponseCode() == HttpURLConnection.HTTP_OK

		when:
		project.tasks.vertxStop.execute()

		and:
		new URL('http://localhost:8080/').openConnection().getResponseCode()

		then:
		thrown ConnectException
	}

}
