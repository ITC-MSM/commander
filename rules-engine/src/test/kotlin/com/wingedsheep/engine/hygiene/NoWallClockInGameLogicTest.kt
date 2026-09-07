package com.wingedsheep.engine.hygiene

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import java.io.File

/**
 * Guards principle §2.1: the rules engine must be a pure function of `(GameState, GameAction)`.
 *
 * `System.currentTimeMillis()` destroys determinism — two floating effects created within the
 * same millisecond sort arbitrarily under Rule 613 layer ordering, which breaks replay,
 * networked state verification, and MCTS simulation.
 *
 * Use `GameState.timestamp` instead — it's monotonic and advances via `state.tick()` at
 * well-defined points (stack resolution, playing a land, cycling, etc.). For extension
 * functions on `GameState`, default `timestamp: Long = this.timestamp` and callers get the
 * right value for free.
 *
 * If this test fails, the fix is almost always "read the timestamp from the receiving `GameState`
 * instead of the system clock."
 *
 * Exceptions — files that are allowed to use wall-clock time because they're AI search time
 * budgets, not game logic — must be listed in [ALLOWED_FILES] with a justification comment.
 *
 * The same guard covers the two other ambient sources of engine nondeterminism: `System.nanoTime()`
 * and `UUID.randomUUID()`. Routing identity (decision, continuation, delayed-trigger and combat-band
 * correlation tokens) comes from `GameState.newRoutingId()`, which allocates from the serialized
 * `nextRoutingId` counter so re-executing the same snapshot reproduces the same tokens — a recorded
 * `SubmitDecision` still addresses the reconstructed prompt. A stray `UUID.randomUUID()` in a
 * routing position silently breaks replay and MCTS simulation while every scenario test stays green,
 * so the ban is enforced here rather than by review.
 */
class NoWallClockInGameLogicTest : FunSpec({

    test("no System.currentTimeMillis() in rules-engine game logic") {
        val offenders = findWallClockUses(
            sourceRoot = sourceRoot(),
            pattern = Regex("""System\.currentTimeMillis\s*\(\s*\)""")
        ).filterNot { it.relativePath in ALLOWED_FILES }

        offenders.map { "${it.relativePath}:${it.lineNumber}: ${it.line.trim()}" }.shouldBeEmpty()
    }

    test("no System.nanoTime() in rules-engine game logic") {
        val offenders = findWallClockUses(
            sourceRoot = sourceRoot(),
            pattern = Regex("""System\.nanoTime\s*\(\s*\)""")
        ).filterNot { it.relativePath in NANO_TIME_ALLOWED_FILES }

        offenders.map { "${it.relativePath}:${it.lineNumber}: ${it.line.trim()}" }.shouldBeEmpty()
    }

    test("no UUID.randomUUID() in rules-engine game logic") {
        val offenders = findWallClockUses(
            sourceRoot = sourceRoot(),
            pattern = Regex("""randomUUID\s*\(\s*\)""")
        )

        offenders.map { "${it.relativePath}:${it.lineNumber}: ${it.line.trim()}" }.shouldBeEmpty()
    }
}) {
    companion object {
        /**
         * Files permitted to use wall-clock time. Each entry must be deliberate — these are
         * places where a real-world deadline is part of the contract, not game state.
         */
        private val ALLOWED_FILES = setOf(
            // AI search deadline: the combat advisor budgets real time for its heuristic
            // exploration. Its output is advisory and not persisted to game state.
            "com/wingedsheep/engine/ai/CombatAdvisor.kt"
        )

        /**
         * Files permitted to read the clock for a *seed*. The value lands in `GameState.rng`, so it
         * is part of the snapshot and replays identically — the ban exists to keep the clock out of
         * decisions and identity, not out of seeding a game that has none.
         */
        private val NANO_TIME_ALLOWED_FILES = setOf(
            "com/wingedsheep/engine/core/GameInitializer.kt"
        )

        private data class WallClockUse(
            val relativePath: String,
            val lineNumber: Int,
            val line: String
        )

        /**
         * Resolves the rules-engine `src/main/kotlin` directory regardless of which directory
         * the test runner is invoked from (module root under Gradle; repo root under some IDEs).
         */
        private fun sourceRoot(): File {
            val candidates = listOf(
                File("src/main/kotlin"),
                File("rules-engine/src/main/kotlin")
            )
            return candidates.firstOrNull { it.isDirectory }
                ?: error("Could not locate rules-engine/src/main/kotlin from ${File(".").absolutePath}")
        }

        private fun findWallClockUses(sourceRoot: File, pattern: Regex): List<WallClockUse> {
            val rootPath = sourceRoot.absolutePath.replace('\\', '/')
            val results = mutableListOf<WallClockUse>()
            sourceRoot.walkTopDown()
                .filter { it.isFile && it.extension == "kt" }
                .forEach { file ->
                    val relative = file.absolutePath.replace('\\', '/').removePrefix("$rootPath/")
                    file.useLines { lines ->
                        lines.forEachIndexed { idx, line ->
                            if (pattern.containsMatchIn(line)) {
                                results += WallClockUse(
                                    relativePath = relative,
                                    lineNumber = idx + 1,
                                    line = line
                                )
                            }
                        }
                    }
                }
            return results
        }
    }
}
