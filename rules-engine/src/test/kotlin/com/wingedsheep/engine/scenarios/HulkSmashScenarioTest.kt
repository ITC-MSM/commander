package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import com.wingedsheep.sdk.scripting.ChoiceSlot
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * HULK SMASH! (MSH #135) — {1}{R} Instant.
 *
 *   Teamwork 4
 *   Choose one. If this spell was cast using teamwork, choose both instead.
 *   • Destroy target noncreature artifact.
 *   • Target creature you control deals damage equal to its power to target creature an opponent
 *     controls.
 *
 * The bite mode carries two targets of its own, so these cases also pin that the damage amount
 * reads *that mode's* first target (the 6/4 Craw Wurm) rather than the modal spell's first target
 * overall (the artifact).
 */
class HulkSmashScenarioTest : ScenarioTestBase() {

    init {
        context("HULK SMASH!") {

            test("cast without teamwork resolves only the one chosen mode") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "HULK SMASH!")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withCardOnBattlefield(2, "Sol Ring")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wurm = game.findPermanent("Craw Wurm").shouldNotBeNull()
                val solRing = game.findPermanent("Sol Ring").shouldNotBeNull()
                val cardId = game.findCardsInHand(1, "HULK SMASH!").first()

                game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = cardId,
                        targets = listOf(ChosenTarget.Permanent(solRing)),
                        chosenModes = listOf(0),
                        modeTargetsOrdered = listOf(listOf(ChosenTarget.Permanent(solRing))),
                    ),
                ).error shouldBe null
                game.resolveStack()

                game.isOnBattlefield("Sol Ring") shouldBe false
                withClue("the bite mode was not chosen, so the 2/2 survives") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
                withClue("no teamwork was declared, so nothing tapped") {
                    game.state.getEntity(wurm)?.has<TappedComponent>() shouldBe false
                }
            }

            test("cast using teamwork resolves both modes") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "HULK SMASH!")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withCardOnBattlefield(2, "Sol Ring")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wurm = game.findPermanent("Craw Wurm").shouldNotBeNull()
                val solRing = game.findPermanent("Sol Ring").shouldNotBeNull()
                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                val cardId = game.findCardsInHand(1, "HULK SMASH!").first()

                // Teamwork 4 — the 6/4 Craw Wurm clears the threshold on its own, and being tapped
                // as a cost does not stop it dealing the bite damage.
                game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = cardId,
                        targets = listOf(
                            ChosenTarget.Permanent(solRing),
                            ChosenTarget.Permanent(wurm),
                            ChosenTarget.Permanent(bears),
                        ),
                        chosenModes = listOf(0, 1),
                        modeTargetsOrdered = listOf(
                            listOf(ChosenTarget.Permanent(solRing)),
                            listOf(ChosenTarget.Permanent(wurm), ChosenTarget.Permanent(bears)),
                        ),
                        declaredCostSlot = ChoiceSlot.TEAMWORK,
                        additionalCostPayment = AdditionalCostPayment(
                            variableCostPermanents = listOf(wurm),
                        ),
                    ),
                ).error shouldBe null
                game.state.getEntity(wurm)?.has<TappedComponent>() shouldBe true

                game.resolveStack()

                game.isOnBattlefield("Sol Ring") shouldBe false
                withClue("6 damage from the Craw Wurm kills the 2/2") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
            }

            test("choosing both modes without teamwork is rejected") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "HULK SMASH!")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withCardOnBattlefield(2, "Sol Ring")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val wurm = game.findPermanent("Craw Wurm").shouldNotBeNull()
                val solRing = game.findPermanent("Sol Ring").shouldNotBeNull()
                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                val cardId = game.findCardsInHand(1, "HULK SMASH!").first()

                game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = cardId,
                        targets = listOf(
                            ChosenTarget.Permanent(solRing),
                            ChosenTarget.Permanent(wurm),
                            ChosenTarget.Permanent(bears),
                        ),
                        chosenModes = listOf(0, 1),
                        modeTargetsOrdered = listOf(
                            listOf(ChosenTarget.Permanent(solRing)),
                            listOf(ChosenTarget.Permanent(wurm), ChosenTarget.Permanent(bears)),
                        ),
                    ),
                ).error.shouldNotBeNull()
                game.isInHand(1, "HULK SMASH!") shouldBe true
            }
        }
    }
}
