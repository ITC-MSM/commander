package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Out of Air (LCI).
 *
 * Oracle: "This spell costs {2} less to cast if it targets a creature spell.\nCounter target spell."
 *
 * Base cost is {2}{U}{U}. The counter half reuses [com.wingedsheep.sdk.dsl.Effects.CounterSpell]
 * over `Targets.Spell` (any spell). The conditional discount is a self-cost reduction —
 * `ModifySpellCost(SelfCast, ReduceGenericBy(FixedIfAnyTargetMatches(2, GameObjectFilter.Creature)))` —
 * gated on the chosen target being a creature *spell on the stack*. It resolves at cast time once the
 * target is announced (CR 601.2f): a creature-spell target drops the generic portion by {2} to {U}{U};
 * any other spell is countered at full price.
 */
class OutOfAirScenarioTest : ScenarioTestBase() {

    /** A minimal spell of [type] sitting on [casterId]'s stack, for cost-reduction target checks. */
    private fun stackSpell(
        casterId: EntityId,
        type: CardType,
        name: String,
    ): Pair<EntityId, ComponentContainer> {
        val id = EntityId.generate()
        val container = ComponentContainer.of(
            CardComponent(
                cardDefinitionId = name,
                name = name,
                manaCost = ManaCost.parse("{1}"),
                typeLine = TypeLine(cardTypes = setOf(type)),
                oracleText = "",
                colors = setOf(Color.GREEN),
                ownerId = casterId,
                spellEffect = null,
            ),
            OwnerComponent(casterId),
        )
        return id to container
    }

    private fun stateWith(casterId: EntityId, spellId: EntityId, container: ComponentContainer): GameState =
        GameState()
            .withEntity(casterId, ComponentContainer.EMPTY)
            .withEntity(spellId, container)
            .addToZone(ZoneKey(casterId, Zone.STACK), spellId)
            .copy(turnOrder = listOf(casterId))

    init {
        context("Out of Air — conditional cost reduction") {

            test("no chosen target → full {2}{U}{U} cost") {
                val casterId = EntityId.generate()
                val (spellId, container) = stackSpell(casterId, CardType.CREATURE, "Grizzly Bears")
                val state = stateWith(casterId, spellId, container)
                val cost = CostCalculator(cardRegistry).calculateEffectiveCost(
                    state, cardRegistry.requireCard("Out of Air"), casterId, chosenTargets = emptyList(),
                )
                cost.toString() shouldBe ManaCost.parse("{2}{U}{U}").toString()
            }

            test("targeting a creature spell reduces the cost by {2} to {U}{U}") {
                val casterId = EntityId.generate()
                val (spellId, container) = stackSpell(casterId, CardType.CREATURE, "Grizzly Bears")
                val state = stateWith(casterId, spellId, container)
                val cost = CostCalculator(cardRegistry).calculateEffectiveCost(
                    state, cardRegistry.requireCard("Out of Air"), casterId, chosenTargets = listOf(spellId),
                )
                cost.toString() shouldBe ManaCost.parse("{U}{U}").toString()
            }

            test("targeting a non-creature spell (instant) leaves the cost at {2}{U}{U}") {
                val casterId = EntityId.generate()
                val (spellId, container) = stackSpell(casterId, CardType.INSTANT, "Shock")
                val state = stateWith(casterId, spellId, container)
                val cost = CostCalculator(cardRegistry).calculateEffectiveCost(
                    state, cardRegistry.requireCard("Out of Air"), casterId, chosenTargets = listOf(spellId),
                )
                cost.toString() shouldBe ManaCost.parse("{2}{U}{U}").toString()
            }
        }

        context("Out of Air — counters target spell") {

            test("with the {2} creature-spell discount it counters a creature spell for just {U}{U}") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Out of Air")
                    .withLandsOnBattlefield(1, "Island", 2) // only {U}{U} — must rely on the discount
                    // Player 2 (active player) casts a creature spell for Out of Air to counter
                    .withCardInHand(2, "Grizzly Bears")
                    .withLandsOnBattlefield(2, "Forest", 2)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val castResult = game.castSpell(2, "Grizzly Bears")
                withClue("Casting Grizzly Bears should succeed: ${castResult.error}") {
                    castResult.error shouldBe null
                }

                game.passPriority()

                // Out of Air's printed cost is {2}{U}{U}=4, but two Islands only make {U}{U}=2 mana.
                // The cast can only succeed if the "{2} less if it targets a creature spell" discount applies.
                val counterResult = game.castSpellTargetingStackSpell(1, "Out of Air", "Grizzly Bears")
                withClue("Casting Out of Air with only {U}{U} should succeed via the discount: ${counterResult.error}") {
                    counterResult.error shouldBe null
                }

                game.resolveStack()

                withClue("Grizzly Bears should be countered — in player 2's graveyard, not on the battlefield") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
            }
        }
    }
}
