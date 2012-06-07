package io.vertx.gradle

import spock.lang.Specification

class VertxDeploySpec extends Specification {

	void 'can interact with a deployed verticle'() {
		expect:
		new URL('http://localhost:8080').text == '<!doctype html><html><head><title>Vert.x Test</title></head><body><p><em>Vert.x</em> is alive!</p></body></html>'
	}

}
