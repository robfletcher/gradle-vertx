package io.vertx.gradle

import org.vertx.java.core.Handler
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import static java.util.concurrent.TimeUnit.SECONDS

class BlockingHandler implements Handler {

	private final CountDownLatch latch = new CountDownLatch(1)

	@Override
	void handle(e) {
		latch.countDown()
	}

	boolean block(int timeout = 5, TimeUnit unit = SECONDS) {
		latch.await(timeout, unit)
	}

}
