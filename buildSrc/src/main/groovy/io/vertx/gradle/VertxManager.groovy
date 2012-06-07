package io.vertx.gradle

import org.vertx.java.deploy.impl.VerticleManager
import org.vertx.java.core.impl.*
import org.vertx.java.deploy.impl.cli.*

import static java.util.concurrent.TimeUnit.SECONDS

@Singleton
class VertxManager {

	private VertxInternal vertx = new DefaultVertx()
	private VerticleManager mgr

	void startVertx() {
		if (!mgr) {
			print 'Starting vertx...'
			mgr = new VerticleManager(vertx)
			println 'OK'

			addShutdownHook {
				stopVertx()
			}
		}
	}

	void stopVertx() {
		if (mgr) {
			def doneHandler = new BlockingHandler()
			print 'Stopping vertx...'
			mgr.undeployAll(doneHandler)
			assert doneHandler.block(10, SECONDS), 'Timed out undeploying verticles'
			println 'OK'

			mgr.unblock()
		}
	}

	String execute(DeployCommand command) {
		// TODO: command.conf
		def doneHandler = new BlockingHandler()
		print "Deploying verticle $command.main..."
		def deploymentName = mgr.deploy(command.worker, command.name, command.main, null, command.urls, command.instances, null, doneHandler)
		assert doneHandler.block(), "Timed out deploying $command.main"
		println 'OK'
		deploymentName
	}

	void execute(UndeployCommand command) {
		def doneHandler = new BlockingHandler()
		print "Undeploying verticle $command.name"
		mgr.undeploy(command.name, doneHandler)
		assert doneHandler.block(), "Timed out undeploying $command.name"
		println 'OK'
	}

	void block() {
		mgr.block()
	}
}
