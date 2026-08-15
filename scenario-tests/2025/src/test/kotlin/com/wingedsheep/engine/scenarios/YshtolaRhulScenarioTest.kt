package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.YshtolaRhul
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Y'shtola Rhul (FIN) — {4}{U}{U} Legendary Creature — Cat Druid 3/5
 *
 * "At the beginning of your end step, exile target creature you control, then return it to the
 *  battlefield under its owner's control. Then if it's the first end step of the turn, there is an
 *  additional end step after this step."
 *
 * Exercises the new [Effects.AddAdditionalEndSteps] / `AdditionalEndStepsComponent` turn-structure
 * path and the [com.wingedsheep.sdk.dsl.Conditions.IsFirstEndStepOfTurn] loop guard.
 *
 * The blink target is a creature whose only ability is "when it enters, you gain 1 life", so each
 * time Y'shtola's end-step trigger resolves the target re-enters and the controller gains 1 life.
 * That makes the life swing a direct count of how many end steps actually happened:
 *  - Without the feature, Y'shtola would trigger once → +1 life.
 *  - With one additional end step (and exactly one — the rider is gated to the *first* end step),
 *    Y'shtola triggers twice → +2 life, and the turn still ends (no infinite loop).
 */
class YshtolaRhulScenarioTest : FunSpec({

    // Blink target: a creature that gains its controller 1 life whenever it enters the battlefield.
    val lifebloomSpirit = card("Lifebloom Spirit") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Spirit"
        power = 1
        toughness = 1
        oracleText = "When Lifebloom Spirit enters the battlefield, you gain 1 life."
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = Effects.GainLife(1)
        }
    }

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(YshtolaRhul)
        driver.registerCard(lifebloomSpirit)
        driver.initMirrorMatch(Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /**
     * Drive the rest of the active player's turn, choosing [blinkTargetName] whenever Y'shtola's
     * end-step trigger asks for a target. Stops once the turn has passed to the opponent (which
     * proves the additional end step terminated instead of looping).
     */
    fun GameTestDriver.finishMyTurn(me: com.wingedsheep.sdk.model.EntityId, blinkTargetName: String) {
        var guard = 0
        while (activePlayer == me && !state.gameOver && guard++ < 200) {
            val decision = pendingDecision
            when {
                decision is ChooseTargetsDecision -> {
                    val target = findPermanent(me, blinkTargetName)
                        ?: error("No '$blinkTargetName' on the battlefield to target")
                    submitTargetSelection(me, listOf(target))
                }
                decision != null -> autoResolveDecision()
                state.priorityPlayerId != null -> passPriority(state.priorityPlayerId!!)
                else -> error("Stuck at step ${state.step} with no priority and no decision")
            }
        }
        if (guard >= 200) error("Turn never ended — additional end step appears to be looping")
    }

    test("Y'shtola adds exactly one additional end step and blinks her target each time") {
        val driver = newDriver()
        val me = driver.player1

        driver.putCreatureOnBattlefield(me, "Y'shtola Rhul")
        driver.putCreatureOnBattlefield(me, "Lifebloom Spirit")

        val lifeBefore = driver.getLifeTotal(me)

        driver.finishMyTurn(me, blinkTargetName = "Lifebloom Spirit")

        // Two end steps happened (the natural one plus exactly one inserted by the rider), so the
        // blink — and its enters-the-battlefield life gain — resolved twice. A third end step would
        // mean the loop guard failed; +2 (not +3) pins down "exactly one extra end step".
        driver.getLifeTotal(me) shouldBe lifeBefore + 2

        // The turn actually ended — control passed to the opponent (further proof there was no loop).
        driver.activePlayer shouldBe driver.player2
    }
})
