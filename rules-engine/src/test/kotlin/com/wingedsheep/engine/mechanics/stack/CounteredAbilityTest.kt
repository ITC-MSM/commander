package com.wingedsheep.engine.mechanics.stack

import com.wingedsheep.engine.core.AbilityCounteredEvent
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.stack.ActivatedAbilityOnStackComponent
import com.wingedsheep.engine.state.components.stack.TriggeredAbilityOnStackComponent
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

/**
 * A countered ability is removed from the stack and ceases to exist (Rule 701.6a), which is why
 * [AbilityCounteredEvent] has to carry the ability's source and controller: after the counter
 * there is nothing left to read them off.
 */
class CounteredAbilityTest : FunSpec({
    val resolver = StackResolver(cardRegistry = CardRegistry())

    test("countering a triggered ability destroys the ability object") {
        val abilityId = EntityId.of("countered-ability")
        val state = GameState(
            entities = mapOf(
                abilityId to ComponentContainer.of(
                    TriggeredAbilityOnStackComponent(
                        sourceId = EntityId.of("source"),
                        sourceName = "Source",
                        controllerId = EntityId.of("controller"),
                        effect = Effects.DrawCards(1),
                        description = "Draw a card",
                    )
                )
            ),
            stack = listOf(abilityId),
        )

        val result = resolver.counterAbility(state, abilityId)

        result.state.stack.shouldBeEmpty()
        result.state.hasEntity(abilityId) shouldBe false
    }

    test("the countered event carries metadata the destroyed ability can no longer supply") {
        val abilityId = EntityId.of("countered-ability")
        val sourceId = EntityId.of("departed-source")
        val controllerId = EntityId.of("ability-controller")
        val state = GameState(
            entities = mapOf(
                abilityId to ComponentContainer.of(
                    TriggeredAbilityOnStackComponent(
                        sourceId = sourceId,
                        sourceName = "Departed Source",
                        controllerId = controllerId,
                        effect = Effects.DrawCards(1),
                        description = "Draw a card",
                    )
                )
            ),
            stack = listOf(abilityId),
        )
        // The source permanent has already left; the ability object was the last thing that knew
        // about it, and countering destroys that too.
        state.getEntity(sourceId) shouldBe null

        val result = resolver.counterAbility(state, abilityId)

        result.state.getEntity(abilityId) shouldBe null
        val event = result.events.filterIsInstance<AbilityCounteredEvent>().single()
        event.sourceId shouldBe sourceId
        event.sourceName shouldBe "Departed Source"
        event.controllerId shouldBe controllerId
    }

    test("an activated ability reports its own controller, not the source's current one") {
        val abilityId = EntityId.of("activated-ability")
        val sourceId = EntityId.of("stolen-source")
        val abilityControllerId = EntityId.of("ability-controller")
        val newSourceControllerId = EntityId.of("new-source-controller")
        val state = GameState(
            entities = mapOf(
                // Rule 113.7a: an ability on the stack exists independently of its source, so the
                // source changing hands afterwards doesn't change who controls the ability.
                sourceId to ComponentContainer.of(ControllerComponent(newSourceControllerId)),
                abilityId to ComponentContainer.of(
                    ActivatedAbilityOnStackComponent(
                        sourceId = sourceId,
                        sourceName = "Stolen Source",
                        controllerId = abilityControllerId,
                        effect = Effects.DrawCards(1),
                    )
                ),
            ),
            stack = listOf(abilityId),
        )

        val result = resolver.counterAbility(state, abilityId)

        val event = result.events.filterIsInstance<AbilityCounteredEvent>().single()
        event.sourceId shouldBe sourceId
        event.sourceName shouldBe "Stolen Source"
        event.controllerId shouldBe abilityControllerId
    }

    test("a stack object with neither ability component is countered without metadata") {
        val abilityId = EntityId.of("bare-stack-object")
        val state = GameState(
            entities = mapOf(abilityId to ComponentContainer.EMPTY),
            stack = listOf(abilityId),
        )

        val event = resolver.counterAbility(state, abilityId)
            .events.filterIsInstance<AbilityCounteredEvent>().single()

        event.description shouldBe "Unknown ability"
        event.sourceId shouldBe null
        event.sourceName shouldBe null
        event.controllerId shouldBe null
    }
})
