package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Roving Actuator (EOE) — {3}{R} Artifact Creature — Robot, 3/4.
 *
 * "Void — When this creature enters, if a nonland permanent left the battlefield this turn or a
 *  spell was warped this turn, exile up to one target instant or sorcery card with mana value 2
 *  or less from your graveyard. Copy it. You may cast the copy without paying its mana cost."
 *
 * The card is pure composition (no new SDK): a Void-gated ETB trigger
 * ([com.wingedsheep.sdk.dsl.Conditions.Void]) wrapping the Shiko-style exile → copy → may-cast
 * pipeline. These tests cover the card's own wiring — the Void gate firing/not-firing and the
 * composite order — rather than the shared pipeline (exercised by ShikoParagonOfTheWayScenarioTest).
 *
 * Void is satisfied by setting [com.wingedsheep.engine.state.GameState.nonlandPermanentLeftBattlefieldThisTurn]
 * directly — the same flag [com.wingedsheep.engine.handlers.ConditionEvaluator] reads for
 * `VoidCondition` — so the trigger condition holds deterministically without staging a separate
 * removal spell.
 */
class RovingActuatorScenarioTest : ScenarioTestBase() {

    init {
        context("Roving Actuator's Void-gated ETB") {

            test("Void satisfied — exiles a graveyard Shock, copies it, casts the copy for free") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Roving Actuator")
                    .withCardInGraveyard(1, "Shock")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardInLibrary(1, "Mountain")
                    .withCardInLibrary(2, "Forest")
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Satisfy Void: a nonland permanent left the battlefield this turn.
                game.state = game.state.copy(nonlandPermanentLeftBattlefieldThisTurn = true)

                val shockId = game.state.getGraveyard(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Shock"
                }

                val cast = game.castSpell(1, "Roving Actuator")
                withClue("Cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                // Roving Actuator enters; its Void-gated ETB fires and pauses for its target.
                game.selectTargets(listOf(shockId))
                // Trigger resolves: exile Shock, copy it, prompt "you may cast the copy".
                game.resolveStack()
                game.answerYesNo(true)
                // The Shock copy is "deals 2 damage to any target" — point it at the opponent.
                game.selectTargets(listOf(game.player2Id))
                game.resolveStack()

                withClue("Shock copy should deal 2 damage to the opponent") {
                    game.getLifeTotal(2) shouldBe 18
                }
                withClue("The original Shock is exiled, not returned to the graveyard") {
                    game.isInGraveyard(1, "Shock") shouldBe false
                }
            }

            test("Void not satisfied — the ETB ability does not trigger") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Roving Actuator")
                    .withCardInGraveyard(1, "Shock")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardInLibrary(1, "Mountain")
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // No nonland permanent left and no spell was warped this turn — Void is off.
                val cast = game.castSpell(1, "Roving Actuator")
                withClue("Cast should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                withClue("With Void off, no target/choice decision should be pending") {
                    game.state.pendingDecision shouldBe null
                }
                withClue("Shock stays in the graveyard — nothing was exiled") {
                    game.isInGraveyard(1, "Shock") shouldBe true
                }
                withClue("No copy was cast, so the opponent took no damage") {
                    game.getLifeTotal(2) shouldBe 20
                }
            }
        }
    }
}
