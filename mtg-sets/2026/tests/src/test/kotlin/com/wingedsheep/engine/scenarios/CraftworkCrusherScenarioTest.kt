package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Craftwork Crusher (FRA #127) — {3}{R}{R}{G}{G} Artifact Creature — Boar Construct, 7/5.
 *
 * "Trample
 *  When this creature enters, choose two —
 *  • This creature deals 4 damage to target creature or planeswalker.
 *  • Create a 2/2 colorless Wizard Soldier creature token named Cadet.
 *  • Draw a card."
 *
 * A choose-two modal *triggered* ability: both modes are picked as the trigger goes on the stack,
 * then the targeted mode's target. Covers a targeted + untargeted pair and an all-untargeted pair.
 */
class CraftworkCrusherScenarioTest : ScenarioTestBase() {

    init {
        fun TestGame.castAndResolveCrusher() {
            castSpell(1, "Craftwork Crusher").error shouldBe null
            if (getPendingDecision() is SelectManaSourcesDecision) submitManaSourcesAutoPay()
            resolveStack()
        }

        fun TestGame.chooseMode(label: String) {
            val decision = state.pendingDecision as? ChooseOptionDecision
                ?: error("expected a ChooseOptionDecision for mode selection; got ${state.pendingDecision}")
            val index = decision.options.indexOfFirst { it.startsWith(label) }
            require(index >= 0) { "mode option '$label' not offered; options=${decision.options}" }
            submitDecision(OptionChosenResponse(decision.id, index))
        }

        fun baseScenario() = scenario()
            .withPlayers("Player1", "Player2")
            .withCardInHand(1, "Craftwork Crusher")
            .withLandsOnBattlefield(1, "Mountain", 4)
            .withLandsOnBattlefield(1, "Forest", 3)
            .withCardInLibrary(1, "Grizzly Bears")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        context("Craftwork Crusher's choose-two ETB trigger") {

            test("damage + draw — the Crusher deals 4 to a creature and you draw a card") {
                val game = baseScenario()
                    .withCardOnBattlefield(2, "Hill Giant")
                    .build()

                val giant = game.findPermanent("Hill Giant")!!
                game.handSize(1) shouldBe 1

                game.castAndResolveCrusher()
                game.chooseMode("This creature deals 4 damage")
                game.chooseMode("Draw a card")

                game.getPendingDecision() as? ChooseTargetsDecision
                    ?: error("expected a ChooseTargetsDecision for the damage mode; got ${game.getPendingDecision()}")
                game.selectTargets(listOf(giant))
                game.resolveStack()

                withClue("Hill Giant (3/3) is destroyed by 4 damage") {
                    game.isInGraveyard(2, "Hill Giant") shouldBe true
                }
                withClue("the draw mode drew the one library card") {
                    game.handSize(1) shouldBe 1
                    game.isInHand(1, "Grizzly Bears") shouldBe true
                }
                withClue("no Cadet — that mode was not chosen") {
                    game.findPermanents("Cadet").size shouldBe 0
                }
            }

            test("Cadet + draw — creates a 2/2 Cadet and draws, no targets needed") {
                val game = baseScenario().build()

                game.castAndResolveCrusher()
                game.chooseMode("Create a 2/2")
                game.chooseMode("Draw a card")
                game.resolveStack()

                val cadets = game.findPermanents("Cadet")
                withClue("exactly one Cadet token") { cadets.size shouldBe 1 }
                val cadet = cadets.single()
                withClue("Cadet is a 2/2") {
                    game.state.projectedState.getPower(cadet) shouldBe 2
                    game.state.projectedState.getToughness(cadet) shouldBe 2
                }
                withClue("drew a card") {
                    game.isInHand(1, "Grizzly Bears") shouldBe true
                }
            }
        }
    }
}
