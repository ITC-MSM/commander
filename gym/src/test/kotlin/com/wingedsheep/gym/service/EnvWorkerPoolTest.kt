package com.wingedsheep.gym.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.util.concurrent.Callable

/**
 * The pool must propagate a task's *own* exception, not the `ExecutionException` its `Future` wraps
 * it in.
 *
 * `GymExceptionHandler` maps `IllegalArgumentException` → 400 and `IllegalStateException` → 409, and
 * an `ExecutionException` matches neither, so before the unwrap a rejected action inside
 * `POST /envs/step-batch` surfaced as a 500 while the identical action on `POST /envs/{id}/step`
 * returned 400 — and so did a *batch of one*, which takes the single-task fast path. Engine
 * rejections are a routine result now that a mis-declared attack raises instead of silently
 * no-opping, so the two paths have to agree.
 */
class EnvWorkerPoolTest : FunSpec({

    val pool = EnvWorkerPool(parallelism = 2)
    afterSpec { pool.close() }

    test("results come back in submission order") {
        val tasks = (1..4).map { n -> Callable { n * n } }
        pool.invokeAll(tasks) shouldBe listOf(1, 4, 9, 16)
    }

    test("a failing task propagates its own exception type, not ExecutionException") {
        val boom = Callable<Int> { throw IllegalArgumentException("rejected by the engine") }

        withClue("a batch of one — the fast path, which never wrapped") {
            shouldThrow<IllegalArgumentException> { pool.invokeAll(listOf(boom)) }
        }
        withClue("a real multi-task batch, which submits to the pool and unwraps the future") {
            val thrown = shouldThrow<IllegalArgumentException> {
                pool.invokeAll(listOf(Callable { 1 }, boom, Callable { 3 }))
            }
            // The *type* is what the exception handler dispatches on, and it survives. The message
            // picks up a class-name prefix on the way: `ForkJoinTask` reconstructs an exception that
            // crosses threads from the original's `toString()`, which is its behaviour, not ours.
            thrown.message shouldContain "rejected by the engine"
        }
    }

    test("an IllegalStateException keeps its identity too, so it can still map to 409") {
        shouldThrow<IllegalStateException> {
            pool.invokeAll(listOf(Callable { 1 }, Callable<Int> { throw IllegalStateException("nope") }))
        }
    }
})
