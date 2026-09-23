package com.wingedsheep.engine.hygiene

import com.wingedsheep.engine.core.EngineServices
import com.wingedsheep.engine.registry.CardRegistry
import io.kotest.core.spec.style.FunSpec
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * Guards the SDK↔engine executor contract: every concrete [com.wingedsheep.sdk.scripting.effects.Effect]
 * subtype must have a registered [com.wingedsheep.engine.handlers.effects.EffectExecutor], or be
 * explicitly declared in [KNOWN_UNEXECUTABLE] with the reason it has none.
 *
 * The bug class this kills: adding an `Effect` subtype to the SDK compiles cleanly without an
 * executor, and `EffectExecutorRegistry.execute` silently returns the unchanged state for
 * unregistered types — so the card "works" but the effect does nothing, surfacing mid-game
 * instead of in CI.
 *
 * If this test fails with a *missing* type: either register an executor for it in the matching
 * `*Executors` module, or — only if the type is genuinely never dispatched through the registry
 * (a marker interpreted structurally by another subsystem) — add it to [KNOWN_UNEXECUTABLE]
 * with a comment saying which subsystem consumes it.
 */
class EffectExecutorCoverageTest : FunSpec({

    val registry = EngineServices(CardRegistry()).effectExecutorRegistry

    test("every concrete Effect subtype has a registered executor or is declared unexecutable") {
        val discovered = findLeafSealedSubclasses(com.wingedsheep.sdk.scripting.effects.Effect::class)
        val registered = registry.registeredEffectTypes()

        val missing = (discovered - registered - KNOWN_UNEXECUTABLE)
            .map { it.qualifiedName ?: it.java.name }
            .sorted()
        if (missing.isNotEmpty()) {
            error(
                "Effect subtypes with no registered executor (register one in the matching " +
                    "*Executors module, or declare the type in KNOWN_UNEXECUTABLE with a reason):\n" +
                    missing.joinToString("\n") { "  - $it" }
            )
        }
    }

    test("KNOWN_UNEXECUTABLE contains no stale entries") {
        val registered = registry.registeredEffectTypes()
        val stale = (KNOWN_UNEXECUTABLE intersect registered)
            .map { it.qualifiedName ?: it.java.name }
            .sorted()
        if (stale.isNotEmpty()) {
            error(
                "Effect types declared KNOWN_UNEXECUTABLE but an executor IS registered " +
                    "(remove them from KNOWN_UNEXECUTABLE):\n" +
                    stale.joinToString("\n") { "  - $it" }
            )
        }
    }
}) {
    companion object {

        /**
         * Effect types that intentionally have no executor. Each entry must say which
         * subsystem consumes it instead. Anything not in this set MUST be registered.
         */
        private val KNOWN_UNEXECUTABLE: Set<KClass<*>> = setOf<KClass<*>>(
        )

        /** Same walker as [SerializationPolymorphicRegistrationTest]. */
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
