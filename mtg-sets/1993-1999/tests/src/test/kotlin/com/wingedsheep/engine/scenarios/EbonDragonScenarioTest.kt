package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario test for Ebon Dragon (POR #91) — {5}{B}{B} Creature — Dragon, 5/4.
 *
 * "Flying
 *  When this creature enters, you may have target opponent discard a card."
 *
 * A "you may" plus a `TargetOpponent` requirement, which makes it the regression case for a
 * fail-open bug in `TriggerProcessor.processTargetedTrigger`: with a single legal opponent the
 * processor auto-selected that opponent and put the trigger straight on the stack, and back when
 * consent rode on the target-selection decision's `minTargets = 0` that skipped the decline
 * entirely — the opponent discarded whether or not the controller wanted them to.
 *
 * The auto-select is back and is fine, because consent is now a gate on the effect and is answered
 * before targeting is reached at all. That is what these two tests pin: the may-question is raised,
 * and each answer does what it says.
 */
class EbonDragonScenarioTest : ScenarioTestBase() {

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

        context("Ebon Dragon's optional ETB discard") {

            test("accepting the may makes the sole opponent discard") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Ebon Dragon")
                    .withCardInHand(2, "Ally Bear")
                    .withLandsOnBattlefield(1, "Swamp", 7)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Ebon Dragon").error shouldBe null
                game.resolveStack()

                withClue("the optional trigger must ask before doing anything") {
                    game.state.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
                }

                game.answerYesNo(true)
                game.resolveStack()

                withClue("Player 2 should have discarded their only card") {
                    game.handSize(2) shouldBe 0
                    game.isInGraveyard(2, "Ally Bear") shouldBe true
                }
            }

            test("declining the may leaves the opponent's hand alone") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Ebon Dragon")
                    .withCardInHand(2, "Ally Bear")
                    .withLandsOnBattlefield(1, "Swamp", 7)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Ebon Dragon").error shouldBe null
                game.resolveStack()

                game.state.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(false)
                game.resolveStack()

                withClue("declining must not make Player 2 discard") {
                    game.handSize(2) shouldBe 1
                    game.isInGraveyard(2, "Ally Bear") shouldBe false
                }

                withClue("Ebon Dragon should still be on the battlefield") {
                    game.isOnBattlefield("Ebon Dragon") shouldBe true
                }
            }
        }
    }
}
