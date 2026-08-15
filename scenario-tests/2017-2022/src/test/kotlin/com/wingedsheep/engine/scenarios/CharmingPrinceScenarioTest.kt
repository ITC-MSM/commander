package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Charming Prince (ELD #8, reprinted FDN #568) — {1}{W} Creature — Human Noble, 2/2.
 *
 * "When this creature enters, choose one —
 *  • Scry 2.
 *  • You gain 3 life.
 *  • Exile another target creature you own. Return it to the battlefield under your control at the
 *    beginning of the next end step."
 *
 * Exercises the modal ETB trigger: the gain-life mode and the exile-then-return-at-end-step blink.
 */
class CharmingPrinceScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(
            CardDefinition.creature(
                name = "Ally Bear",
                manaCost = ManaCost.parse("{1}{G}"),
                subtypes = setOf(Subtype("Bear")),
                power = 2,
                toughness = 2
            )
        )

        context("Charming Prince's ETB modal trigger") {

            test("mode 2 gains 3 life") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Charming Prince")
                    .withLifeTotal(1, 20)
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Charming Prince").error shouldBe null
                game.resolveStack()

                val modeDecision = game.state.pendingDecision as? ChooseOptionDecision
                    ?: error("expected a ChooseOptionDecision; got ${game.state.pendingDecision}")
                game.submitDecision(OptionChosenResponse(modeDecision.id, optionIndex = 1))
                game.resolveStack()

                withClue("Player 1 should have gained 3 life (20 -> 23)") {
                    game.getLifeTotal(1) shouldBe 23
                }
            }

            test("mode 3 exiles a creature you own and returns it at the next end step") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Charming Prince")
                    .withCardOnBattlefield(1, "Ally Bear", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Charming Prince").error shouldBe null
                game.resolveStack()

                val modeDecision = game.state.pendingDecision as? ChooseOptionDecision
                    ?: error("expected a ChooseOptionDecision; got ${game.state.pendingDecision}")
                game.submitDecision(OptionChosenResponse(modeDecision.id, optionIndex = 2))

                val targetDecision = game.state.pendingDecision as? ChooseTargetsDecision
                    ?: error("expected a ChooseTargetsDecision after mode pick; got ${game.state.pendingDecision}")
                val bear = game.findPermanent("Ally Bear")!!
                game.submitDecision(TargetsResponse(targetDecision.id, mapOf(0 to listOf(bear))))
                game.resolveStack()

                withClue("Ally Bear should have been exiled off the battlefield") {
                    game.findPermanent("Ally Bear") shouldBe null
                }

                // Advance to the end step; the delayed trigger returns the exiled creature.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("Ally Bear should be back on the battlefield after the next end step") {
                    (game.findPermanent("Ally Bear") != null) shouldBe true
                }
            }
        }
    }
}
