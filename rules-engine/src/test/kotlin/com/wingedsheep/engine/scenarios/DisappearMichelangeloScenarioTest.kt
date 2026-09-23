package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import com.wingedsheep.engine.core.Outcome

/**
 * Disappear intervening-if test, exercised through Michelangelo, Game Master (TMT):
 * "Disappear — At the beginning of your end step, if a permanent left the battlefield
 *  under your control this turn, put a +1/+1 counter on Michelangelo."
 *
 * Disappear reuses the existing per-controller `PermanentLeftBattlefieldThisTurnComponent`
 * tracker (set in ZoneTransitionService for any permanent — lands included — leaving that
 * player's battlefield) via the `Conditions.YouHadPermanentLeaveBattlefieldThisTurn`
 * intervening-if (CR 603.4). These tests prove the condition gates the end-step trigger:
 * the counter lands only when a permanent actually left this turn.
 */
class DisappearMichelangeloScenarioTest : FunSpec({

    val projector = StateProjector()

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCards(PredefinedTokens.allTokens) // GameTestDriver doesn't auto-register tokens
        return d
    }

    test("a permanent leaving this turn satisfies Disappear — counter is placed") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val p = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val mike = d.putCreatureOnBattlefield(p, "Michelangelo, Game Master") // 3/3
        // Sacrifice a Food via its own ability so a permanent genuinely leaves the
        // battlefield through ZoneTransitionService (which credits the controller).
        val food = d.putPermanentOnBattlefield(p, "Food")
        d.giveMana(p, Color.GREEN, 2)
        d.submit(
            ActivateAbility(
                playerId = p,
                sourceId = food,
                abilityId = PredefinedTokens.Food.activatedAbilities.first().id
            )
        ).outcome shouldBe Outcome.Done
        var guard = 0
        while (d.stackSize > 0 && guard++ < 6) d.bothPass()

        // Advance to the end step; the Disappear trigger fires (intervening-if true) and resolves.
        d.passPriorityUntil(Step.END)
        guard = 0
        while (d.stackSize > 0 && guard++ < 6) d.bothPass()

        projector.getProjectedPower(d.state, mike) shouldBe 4
        projector.getProjectedToughness(d.state, mike) shouldBe 4
    }

    test("no permanent left this turn — Disappear does not trigger, no counter") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val p = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val mike = d.putCreatureOnBattlefield(p, "Michelangelo, Game Master")

        d.passPriorityUntil(Step.END)
        var guard = 0
        while (d.stackSize > 0 && guard++ < 6) d.bothPass()

        // Intervening-if is false (nothing left the battlefield), so the trigger never resolves.
        projector.getProjectedPower(d.state, mike) shouldBe 3
        projector.getProjectedToughness(d.state, mike) shouldBe 3
    }
})
