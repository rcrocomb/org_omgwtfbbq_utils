#!/usr/bin/env groovy

package com.shutterfly.lti.util

import static com.shutterfly.lti.util.LogUtil.*

import groovy.util.logging.Commons

import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService

@Commons
abstract class ThreadUtil {
	private ThreadUtil() {}

	/*
		ExecutorCompletionService, not Elastic Container Service
	*/

	static void stopECS(ecs, executorService) {
		if (!ecs)
			return

		log.debug("shutdown ECS")
		executorService.shutdown()

		boolean didQuit = false
		long future = System.currentTimeMillis() + 5 * 1000
		for (int j = 0 ; ; ++j) {
			if (executorService.isShutdown()) {
				log.debug("Good.. everything is shutdown")
				didQuit = true
				break
			}
			if (System.currentTimeMillis() > future) {
				log.warn("Ran out of time -- bailing!")
				break
			}
			Thread.sleep(100)
		}
		if (!didQuit) {
			log.debug("Forcing shutdown")
			ecs.shutdownNow()
		}
		log.debug("Done with ECS")
	}

	/*
		Fancy!  I decided to change to polling Thread state because I
		want to join against whatever thread is ready (state == TERMINATED)
		first, rather than in thread array order.  I want this because
		I believe that I'll need to terminate threads that are still running:
		I don't want the behavior that one thread encounters an error and
		terminates while the others keep running for hours or whatever.
	*/

	static void waitForThreadCompletion(threads) {
		// Only send interrupt() to other Threads once.
		boolean hasInterrupted = false
		// This is for "edge-detection" of state change: if "now" != previous, state
		// has changed and we may want to do something.
		def previousState = [:] // index --> Thread.State.  Can't use thread.name because we change it so often
		threads.eachWithIndex { it, i ->
			// Uhhh, just use 'NEW' in case any thread dies super fast and is TERMINATED already
			// Otherwise you won't see the "edge"
			previousState[i] = Thread.State.NEW
			debug("[%2d] Thread state for ${it.name} initialized to ${previousState[i]}", i)
		}

		// Until all threads are done
		for (int iteration = 0 ; ; ++iteration) {
			threads.eachWithIndex { Thread t, int i ->
				if (t.state != previousState[i]) {
					info("[%6d][%2d] Thread ${t.name} has changed state from ${previousState[i]} --> ${t.state}", iteration, i)
					if (t.state == Thread.State.TERMINATED) {
						// My hope is that by checking that t.isInterrupted() that we won't shutdown
						// other threads simply because this thread has finished running.  Otherwise I've
						// got to add smarts to the threads to set a variable on error or something.
						if (t.isInterrupted() && !hasInterrupted) {
							hasInterrupted = true
							threads.eachWithIndex { it, j ->
								if (i == j) { return }
								it.interrupt()
								debug("[%6d][%2d] interrupting thread ${it.name}", iteration, j)
							}
						}
						debug("[%6d][%2d] Joining against ${t.name}", iteration, i)
						t.join()
						debug("[%6d][%2d] Join complete for ${t.name}", iteration, i)
					}
					previousState[i] = t.state
				} // else no state change for thread 't'
			}

			if (threads.findAll { it.state != Thread.State.TERMINATED }.isEmpty()) {
				debug("[%6d] All threads have terminated.  We out", iteration)
				threads.eachWithIndex { t, i -> debug("[%6d][%2d] %s --> %s", iteration, i, t.name, t.state) }
				break
			}
			debug("[%6d] waiting for thread state change", iteration)
			Thread.sleep(1000)
		}
	}
}
