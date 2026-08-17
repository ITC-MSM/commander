package com.wingedsheep.gym.service

import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit

/**
 * Thread pool for running per-environment operations in parallel.
 *
 * Each [com.wingedsheep.gym.GameEnvironment] is single-threaded —
 * `reset` / `step` on a given env must not race with itself. But different
 * envs are independent, so N envs can run in N threads. The pool schedules
 * per-env tasks; callers of [MultiEnvService.stepBatch] fan out through here.
 *
 * Uses [ForkJoinPool] rather than a fixed thread pool so nested work (future
 * MCTS rollouts that internally fork envs) doesn't starve.
 */
class EnvWorkerPool(
    parallelism: Int = Runtime.getRuntime().availableProcessors()
) {
    private val pool = ForkJoinPool(parallelism)

    /**
     * Submit independent tasks and wait for all of them, preserving order.
     *
     * A task that throws propagates its *own* exception, not the [ExecutionException] the future
     * wraps it in. That wrapping is not cosmetic: `GymExceptionHandler` maps
     * `IllegalArgumentException` to 400 and `IllegalStateException` to 409, and an
     * `ExecutionException` matches neither, so a rejected action in a batch used to surface as a
     * 500 while the same action posted to `/envs/{id}/step` — or in a batch of one, which takes the
     * fast path below — correctly returned 400.
     */
    fun <T> invokeAll(tasks: List<Callable<T>>): List<T> {
        if (tasks.isEmpty()) return emptyList()
        if (tasks.size == 1) return listOf(tasks.single().call())
        val futures = tasks.map { pool.submit(it) }
        return futures.map { future ->
            try {
                future.get()
            } catch (e: ExecutionException) {
                throw e.cause ?: e
            }
        }
    }

    /** Shut the pool down gracefully; awaits in-flight tasks. */
    fun close(awaitSeconds: Long = 5) {
        pool.shutdown()
        pool.awaitTermination(awaitSeconds, TimeUnit.SECONDS)
    }

    val parallelism: Int get() = pool.parallelism
}
