package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Crossway Troublemakers (VOC #17; reprinted in Foundations).
 *
 * Attacking Vampires you control have deathtouch and lifelink.
 * Whenever a Vampire you control dies, you may pay 2 life. If you do, draw a card.
 *
 * Covers the "attacking" gate on the deathtouch/lifelink static and the death trigger's optional
 * 2-life payment to draw.
 */
class CrosswayTroublemakersScenarioTest : ScenarioTestBase() {

    init {
        context("Crossway Troublemakers") {

            test("grants deathtouch and lifelink only while a Vampire you control is attacking") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Crossway Troublemakers", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val crossway = game.findPermanent("Crossway Troublemakers")!!

                withClue("Not attacking yet, so no combat keywords") {
                    game.state.projectedState.hasKeyword(crossway, Keyword.DEATHTOUCH) shouldBe false
                    game.state.projectedState.hasKeyword(crossway, Keyword.LIFELINK) shouldBe false
                }

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Crossway Troublemakers" to 2)).error shouldBe null

                withClue("While attacking, this Vampire gains deathtouch and lifelink") {
                    game.state.projectedState.hasKeyword(crossway, Keyword.DEATHTOUCH) shouldBe true
                    game.state.projectedState.hasKeyword(crossway, Keyword.LIFELINK) shouldBe true
                }
            }

            test("a dying Vampire lets you pay 2 life to draw a card") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Crossway Troublemakers", summoningSickness = false)
                    .withCardOnBattlefield(1, "Indulgent Aristocrat") // a Vampire you control
                    .withLandsOnBattlefield(1, "Swamp", 3) // pays {1}{B}{B} for Murder
                    .withCardInHand(1, "Murder")
                    .withCardInLibrary(1, "Swamp") // something to draw
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val aristocrat = game.findPermanent("Indulgent Aristocrat")!!
                val lifeBefore = game.getLifeTotal(1)

                game.castSpell(1, "Murder", aristocrat).error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("The Vampire's death offers the optional 2-life payment") {
                    game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                }
                val handBefore = game.handSize(1)
                game.answerYesNo(true)
                game.resolveStack()

                withClue("Paying 2 life then draws a card") {
                    game.getLifeTotal(1) shouldBe (lifeBefore - 2)
                    game.handSize(1) shouldBe (handBefore + 1)
                }
            }
        }
    }
}
