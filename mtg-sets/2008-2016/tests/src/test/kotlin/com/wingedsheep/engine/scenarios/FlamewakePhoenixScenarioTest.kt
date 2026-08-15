package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Flamewake Phoenix (FRF #100; reprinted in Foundations).
 *
 * Ferocious — At the beginning of combat on your turn, if you control a creature with power 4 or
 * greater, you may pay {R}. If you do, return this card from your graveyard to the battlefield.
 *
 * Covers the graveyard-zone combat trigger, the Ferocious intervening-if gate, and the optional
 * {R} payment that returns the phoenix. All primitives already exist (BeginCombat trigger,
 * YouControl condition, MayPayManaEffect, Move to battlefield).
 */
class FlamewakePhoenixScenarioTest : ScenarioTestBase() {

    init {
        context("Flamewake Phoenix") {

            test("Ferocious met + paying {R} returns the phoenix from the graveyard") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInGraveyard(1, "Flamewake Phoenix")
                    .withCardOnBattlefield(1, "Craw Wurm") // 6/4 — a creature with power 4 or greater
                    .withLandsOnBattlefield(1, "Mountain", 1) // pays {R}
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                // The begin-combat trigger is queued on the stack; resolve it to surface the may-pay.
                game.resolveStack()

                withClue("Ferocious trigger offers the optional {R} payment") {
                    game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                }
                game.answerYesNo(true)
                game.getPendingDecision().shouldBeInstanceOf<SelectManaSourcesDecision>()
                game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("Phoenix is returned to the battlefield") {
                    (game.findPermanent("Flamewake Phoenix") != null) shouldBe true
                }
                withClue("Phoenix is no longer in the graveyard") {
                    game.isInGraveyard(1, "Flamewake Phoenix") shouldBe false
                }
            }

            test("declining the payment leaves the phoenix in the graveyard") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInGraveyard(1, "Flamewake Phoenix")
                    .withCardOnBattlefield(1, "Craw Wurm")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                game.resolveStack()

                game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(false)
                game.resolveStack()

                withClue("Phoenix stays in the graveyard when the payment is declined") {
                    game.isInGraveyard(1, "Flamewake Phoenix") shouldBe true
                }
            }

            test("without a power-4 creature, the Ferocious trigger does not fire") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInGraveyard(1, "Flamewake Phoenix")
                    .withCardOnBattlefield(1, "Grizzly Bears") // 2/2 — not power 4 or greater
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                game.resolveStack()

                withClue("No payment prompt because the Ferocious gate is not met") {
                    (game.getPendingDecision() is YesNoDecision) shouldBe false
                }
                withClue("Phoenix remains in the graveyard") {
                    game.isInGraveyard(1, "Flamewake Phoenix") shouldBe true
                }
            }
        }
    }
}
