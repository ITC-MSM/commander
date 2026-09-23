package com.wingedsheep.engine.event

import com.wingedsheep.engine.core.TriggerDamageDistributionContinuation
import com.wingedsheep.engine.core.TriggeredAbilityContinuation
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.state.components.stack.TriggeredAbilityOnStackComponent
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import java.lang.reflect.Modifier

/**
 * Pins "a new trigger fact is one field on [TriggerContext]; carriers hold the whole record".
 *
 * Before the record, every fact (`triggerScryCount`, `triggerClashWon`, `lastKnownPower`, …) was
 * re-declared on each carrier and copied field by field — adding `clashWon` touched 18 files. If
 * this fails, put the new fact on [TriggerContext] and read it as `context.triggerContext?.x`
 * instead of adding a field here.
 */
class TriggerContextCarrierInvariantTest : FunSpec({
    /** Names that are trigger facts under another spelling, once flat on the carriers. */
    val unprefixedFacts = setOf(
        "lastKnownPower", "lastKnownToughness", "diedBatchTotalPower", "capturedEntityIds",
        "enchantedCreatureLastKnownPower", "targetingSourceEntityId",
    )

    fun instanceFields(type: Class<*>): List<String> =
        type.declaredFields.filterNot { Modifier.isStatic(it.modifiers) || it.isSynthetic }.map { it.name }

    fun perFactFields(type: Class<*>, allowed: Set<String>): List<String> =
        instanceFields(type).filter { (it.startsWith("trigger") || it in unprefixedFacts) && it !in allowed }

    test("the triggered-ability stack object carries only the record") {
        perFactFields(TriggeredAbilityOnStackComponent::class.java, setOf("triggerContext")).shouldBeEmpty()
    }

    test("the target-selection and damage-distribution frames carry only the record") {
        perFactFields(TriggeredAbilityContinuation::class.java, setOf("triggerContext")).shouldBeEmpty()
        perFactFields(TriggerDamageDistributionContinuation::class.java, setOf("triggerContext")).shouldBeEmpty()
    }

    test("EffectContext carries only the record plus its rebindable triggering slots") {
        // Justified exceptions: iteration, spell resolution, delayed triggers and cast-time copies
        // write these without any trigger record, so they are context slots, not trigger facts.
        // `triggeringReferenceLost` is object-identity bookkeeping derived at resolution.
        val allowed = setOf("triggerContext", "triggeringEntityId", "triggeringPlayerId", "triggeringReferenceLost")
        perFactFields(EffectContext::class.java, allowed).shouldBeEmpty()
    }

    test("every carrier really does hold the record") {
        for (type in listOf(
            TriggeredAbilityOnStackComponent::class.java, TriggeredAbilityContinuation::class.java,
            TriggerDamageDistributionContinuation::class.java, EffectContext::class.java,
        )) {
            type.getDeclaredField("triggerContext").type shouldBe TriggerContext::class.java
        }
    }
})
