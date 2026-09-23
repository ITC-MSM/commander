package com.wingedsheep.engine.hygiene

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import java.io.File

/**
 * Guards the single settle boundary: [com.wingedsheep.engine.core.Settler] is the only code that
 * turns events into triggered abilities.
 *
 * Before it existed, some 17 handlers and resumers each ran their own detect → state-based actions
 * → stack → priority loop. A trigger whose events passed through two of them was detected twice,
 * and an `ExecutionResult.triggersAlreadyProcessed` flag was threaded through the engine to
 * suppress the second detection. Every copy of the loop was one more place for that flag to be
 * dropped, which is how the double-trigger bugs cited in its old KDoc happened.
 *
 * If this test fails, don't detect: emit the events and let the boundary queue the triggers in
 * `GameState.pendingTriggers`. That is also correct across a pause, because the boundary parks
 * them until the question is answered.
 */
class SingleSettleBoundaryTest : FunSpec({

    test("only the settle boundary detects triggers from events") {
        val offenders = findUses(Regex("""\.detectTriggers\s*\(""")) { it !in DETECTION_SITES }
        offenders.shouldBeEmpty()
    }
}) {
    companion object {
        private val DETECTION_SITES = setOf("com/wingedsheep/engine/core/Settler.kt")

        private fun sourceRoot(): File =
            listOf(File("src/main/kotlin"), File("rules-engine/src/main/kotlin"))
                .firstOrNull { it.isDirectory }
                ?: error("Could not locate rules-engine/src/main/kotlin from ${File(".").absolutePath}")

        private fun findUses(pattern: Regex, include: (String) -> Boolean): List<String> {
            val root = sourceRoot()
            val rootPath = root.absolutePath.replace('\\', '/')
            return root.walkTopDown()
                .filter { it.isFile && it.extension == "kt" }
                .flatMap { file ->
                    val relative = file.absolutePath.replace('\\', '/').removePrefix("$rootPath/")
                    if (!include(relative)) return@flatMap emptySequence()
                    file.readLines().withIndex()
                        .filter { (_, line) -> pattern.containsMatchIn(line) && !line.trimStart().startsWith("*") && !line.trimStart().startsWith("//") }
                        .map { (idx, line) -> "$relative:${idx + 1}: ${line.trim()}" }
                        .asSequence()
                }
                .toList()
        }
    }
}
