package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Shapesharer (LRW #85) — "{2}{U}: Target Shapeshifter becomes a copy of target creature until
 * your next turn."
 *
 * The card is pure composition over `EachPermanentBecomesCopyOfTarget`, so what is worth proving is
 * the two things composition can silently get wrong:
 *
 *  - **Which target is which.** `affected` (index 0, the Shapeshifter) and `target` (index 1, the
 *    copy source) are both `ContextTarget`s of the same shape; swapping them still compiles, still
 *    reads right on the card, and turns "the Shapeshifter becomes a Giant" into "the Giant becomes
 *    a Shapeshifter". The test aims the two at *different* permanents controlled by *different*
 *    players, which is the only board where the swap is visible.
 *  - **The duration.** "Until your next turn" is a strictly longer window than the end-of-turn
 *    cleanup that most copy effects use, so the copy has to survive the whole of the opponent's
 *    turn and wear off only after the untap step of the controller's next one.
 */
class ShapesharerScenarioTest : ScenarioTestBase() {

    init {
        context("Shapesharer") {

            // The duration case runs three turns, so both libraries need cards — a draw from an
            // empty library ends the game (CR 704.5b) and silently stalls the turn advance.
            fun board(): ScenarioTestBase.TestGame {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Shapesharer", summoningSickness = false)
                    .withCardOnBattlefield(1, "Fire-Belly Changeling", summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) {
                    builder = builder.withCardInLibrary(1, "Island").withCardInLibrary(2, "Island")
                }
                return builder.build()
            }

            fun ScenarioTestBase.TestGame.reshape(shapeshifter: EntityId, source: EntityId) {
                val abilityId = cardRegistry.getCard("Shapesharer")!!.script.activatedAbilities[0].id
                val result = execute(
                    ActivateAbility(
                        playerId = player1Id,
                        sourceId = findPermanent("Shapesharer")!!,
                        abilityId = abilityId,
                        targets = listOf(
                            entityIdToChosenTarget(state, shapeshifter),
                            entityIdToChosenTarget(state, source),
                        )
                    )
                )
                withClue("activating Shapesharer should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                if (getPendingDecision() is SelectManaSourcesDecision) submitManaSourcesAutoPay()
                resolveStack()
            }

            /**
             * Advance to the next player's upkeep. Routed through the end step on purpose:
             * `passUntilPhase` returns immediately when the game is already at the requested
             * phase/step, so two upkeep hops in a row would silently be a single hop.
             */
            fun ScenarioTestBase.TestGame.advanceToNextUpkeep() {
                passUntilPhase(Phase.ENDING, Step.END)
                passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
            }

            test("the targeted Shapeshifter becomes the other creature, and the copy source is untouched") {
                val game = board()
                val changeling = game.findPermanent("Fire-Belly Changeling")!!
                val giant = game.findPermanent("Hill Giant")!!

                game.reshape(shapeshifter = changeling, source = giant)

                withClue("index 0 is the Shapeshifter that changes") {
                    game.state.getEntity(changeling)!!.get<CardComponent>()!!.name shouldBe "Hill Giant"
                    game.state.projectedState.getPower(changeling) shouldBe 3
                    game.state.projectedState.getToughness(changeling) shouldBe 3
                }
                withClue("index 1 is only the copy source — the opponent's Giant is unchanged") {
                    game.state.getEntity(giant)!!.get<CardComponent>()!!.name shouldBe "Hill Giant"
                }
                withClue("Shapesharer itself was not the affected permanent") {
                    val shapesharer = game.findPermanent("Shapesharer")!!
                    game.state.getEntity(shapesharer)!!.get<CardComponent>()!!.name shouldBe "Shapesharer"
                }
            }

            test("the copy survives the opponent's whole turn and wears off on your next one") {
                val game = board()
                val changeling = game.findPermanent("Fire-Belly Changeling")!!
                val giant = game.findPermanent("Hill Giant")!!

                game.reshape(shapeshifter = changeling, source = giant)

                game.advanceToNextUpkeep()
                withClue("still a Hill Giant during the opponent's turn — this is not end-of-turn") {
                    game.state.activePlayerId shouldBe game.player2Id
                    game.state.getEntity(changeling)!!.get<CardComponent>()!!.name shouldBe "Hill Giant"
                }

                game.advanceToNextUpkeep()
                withClue("reverted after the untap step of the controller's next turn") {
                    game.state.activePlayerId shouldBe game.player1Id
                    game.state.getEntity(changeling)!!
                        .get<CardComponent>()!!.name shouldBe "Fire-Belly Changeling"
                    game.state.projectedState.getPower(changeling) shouldBe 1
                }
            }
        }
    }
}
