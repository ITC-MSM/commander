package com.wingedsheep.engine.hygiene

import com.wingedsheep.engine.core.AnswerContinuation
import com.wingedsheep.engine.core.AutomaticContinuation
import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.registry.CardRegistry
import io.kotest.core.spec.style.FunSpec
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * Every continuation frame the engine can push must have something that resumes it. This is the
 * continuation-side twin of [EffectExecutorCoverageTest].
 *
 * - An [AnswerContinuation] with no registered resumer fails only at runtime:
 *   `ContinuationResumerRegistry.resume` returns "No resumer registered", after the player has
 *   already answered. This test turns that into a build failure.
 * - An [AutomaticContinuation] with no auto-resumer is skipped by `tryAutoResume` and sits on the
 *   stack until some other code pops it by type. Some frames are designed that way, and they are
 *   listed in [STRUCTURAL_AUTOMATIC] with the code that consumes them. Any other frame without an
 *   auto-resumer is stranded work.
 */
class ContinuationResumerCoverageTest : FunSpec({

    val handler = EngineServices(CardRegistry()).continuationHandler

    test("every AnswerContinuation has a registered resumer") {
        val discovered = findLeafSealedSubclasses(AnswerContinuation::class)
        check(discovered.size > 100) { "walker found only ${discovered.size} AnswerContinuation types" }
        val missing = (discovered - handler.registeredAnswerTypes())
            .map { it.qualifiedName ?: it.java.name }
            .sorted()
        if (missing.isNotEmpty()) {
            error(
                "AnswerContinuation types with no registered resumer (add one to the matching " +
                    "*ContinuationResumer module and register it in ContinuationHandler):\n" +
                    missing.joinToString("\n") { "  - $it" }
            )
        }
    }

    test("every AutomaticContinuation has an auto-resumer or is declared structural") {
        val missing = (
            findLeafSealedSubclasses(AutomaticContinuation::class) -
                handler.registeredAutomaticTypes() -
                STRUCTURAL_AUTOMATIC
            )
            .map { it.qualifiedName ?: it.java.name }
            .sorted()
        if (missing.isNotEmpty()) {
            error(
                "AutomaticContinuation types with no auto-resumer (register one in an " +
                    "AutoResumerModule, or declare the frame in STRUCTURAL_AUTOMATIC with the code " +
                    "that pops it):\n" + missing.joinToString("\n") { "  - $it" }
            )
        }
    }

    test("STRUCTURAL_AUTOMATIC contains no stale entries") {
        val stale = (STRUCTURAL_AUTOMATIC intersect handler.registeredAutomaticTypes())
            .map { it.qualifiedName ?: it.java.name }
            .sorted()
        if (stale.isNotEmpty()) {
            error(
                "Frames declared STRUCTURAL_AUTOMATIC but an auto-resumer IS registered " +
                    "(remove them from STRUCTURAL_AUTOMATIC):\n" + stale.joinToString("\n") { "  - $it" }
            )
        }
    }
}) {
    companion object {

        /**
         * Automatic frames that are popped by type from a specific code path rather than through
         * `tryAutoResume`. Each entry names that code path.
         */
        private val STRUCTURAL_AUTOMATIC: Set<KClass<*>> = setOf<KClass<*>>(
        )

        /** Same walker as [EffectExecutorCoverageTest]. */
        private fun findLeafSealedSubclasses(base: KClass<*>): Set<KClass<*>> {
            require(base.isSealed) { "${base.qualifiedName} must be sealed to be walked reflectively" }
            val leaves = mutableSetOf<KClass<*>>()
            val queue: ArrayDeque<KClass<*>> = ArrayDeque(base.sealedSubclasses)
            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                val children = current.sealedSubclasses
                if (children.isNotEmpty()) {
                    queue.addAll(children)
                    continue
                }
                val javaClass = current.java
                if (javaClass.isInterface) continue
                if (Modifier.isAbstract(javaClass.modifiers)) continue
                leaves.add(current)
            }
            return leaves
        }
    }
}
