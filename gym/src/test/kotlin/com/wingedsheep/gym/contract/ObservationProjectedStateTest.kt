package com.wingedsheep.gym.contract

import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ActivatedAbilityOnStackComponent
import com.wingedsheep.engine.state.components.stack.SpellOnStackComponent
import com.wingedsheep.engine.state.components.stack.TriggeredAbilityOnStackComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.blc.cards.RollingHamsphere
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.dsl.Effects
import io.kotest.matchers.shouldBe

/** Public Gym fields must read the engine semantic authority for the object kind in question. */
class ObservationProjectedStateTest : ScenarioTestBase() {

    init {
        cardRegistry.register(RollingHamsphere)

        test("battlefield names follow the layer projection") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Test Hasty Prospector")
                .withCardOnBattlefield(1, "Witness Protection")
                .build()
            val creature = permanentNamed(game.state, "Test Hasty Prospector")
            val aura = permanentNamed(game.state, "Witness Protection")
            val enchanted = game.state.updateEntity(aura) { it.with(AttachedToComponent(creature)) }

            enchanted.projectedState.getName(creature) shouldBe "Legitimate Businessperson"
            entity(observe(enchanted, game.player1Id), creature).name shouldBe
                "Legitimate Businessperson"
        }

        test("an uncrewed Vehicle has no public power or toughness") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, RollingHamsphere.name)
                .build()
            val vehicle = permanentNamed(game.state, RollingHamsphere.name)

            game.state.projectedState.isCreature(vehicle) shouldBe false
            // Projection retains the printed values for a later animation, but public P/T is
            // conditional on the object currently being a creature.
            game.state.projectedState.getPower(vehicle) shouldBe 4
            entity(observe(game.state, game.player1Id), vehicle).let {
                it.power shouldBe null
                it.toughness shouldBe null
            }
        }

        test("haste suppresses effective summoning sickness in the observation") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(
                    1,
                    "Test Hasty Prospector",
                    summoningSickness = true,
                )
                .build()
            val creature = permanentNamed(game.state, "Test Hasty Prospector")

            game.state.projectedState.hasKeyword(creature, Keyword.HASTE) shouldBe true
            entity(observe(game.state, game.player1Id), creature).summoningSick shouldBe false
        }

        test("stack controller and kind come from stack components rather than battlefield projection") {
            val game = scenario()
                .withPlayers()
                .withCardInHand(2, "Hill Giant")
                .build()
            val spell = game.state.getHand(game.player2Id).single()
            val trigger = EntityId.of("trigger-on-stack")
            val activation = EntityId.of("activation-on-stack")
            val state = game.state
                .removeFromZone(ZoneKey(game.player2Id, Zone.HAND), spell)
                .updateEntity(spell) { it.with(SpellOnStackComponent(casterId = game.player2Id)) }
                .withEntity(
                    trigger,
                    ComponentContainer.of(
                        TriggeredAbilityOnStackComponent(
                            sourceId = EntityId.of("trigger-source"),
                            sourceName = "Trigger Source",
                            controllerId = game.player1Id,
                            effect = Effects.DrawCards(1),
                            description = "Draw a card",
                        ),
                    ),
                )
                .withEntity(
                    activation,
                    ComponentContainer.of(
                        ActivatedAbilityOnStackComponent(
                            sourceId = EntityId.of("activation-source"),
                            sourceName = "Activation Source",
                            controllerId = game.player2Id,
                            effect = Effects.DrawCards(1),
                        ),
                    ),
                )
                .copy(stack = listOf(spell, trigger, activation))

            val stack = observe(state, game.player1Id).stack.associateBy { it.entityId }
            stack.getValue(spell).let {
                it.kind shouldBe StackItemKind.SPELL
                it.controllerId shouldBe game.player2Id
            }
            stack.getValue(trigger).let {
                it.kind shouldBe StackItemKind.TRIGGERED_ABILITY
                it.controllerId shouldBe game.player1Id
            }
            stack.getValue(activation).let {
                it.kind shouldBe StackItemKind.ACTIVATED_ABILITY
                it.controllerId shouldBe game.player2Id
            }
        }
    }

    private fun observe(
        state: com.wingedsheep.engine.state.GameState,
        viewer: EntityId,
    ): TrainingObservation = ObservationBuilder()
        .build(state, viewer, legalActions = emptyList())
        .observation as TrainingObservation

    private fun entity(observation: TrainingObservation, entityId: EntityId): EntityFeatures =
        observation.zones.flatMap { it.cards }.single { it.entityId == entityId }

    private fun permanentNamed(
        state: com.wingedsheep.engine.state.GameState,
        name: String,
    ): EntityId = state.getBattlefield().single { id ->
        state.getEntity(id)?.get<CardComponent>()?.name == name
    }
}
