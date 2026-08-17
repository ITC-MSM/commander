package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Last-known source P/T on a self-sacrifice cost (CR 113.7a / 608.2h).
 *
 * "{T}, Sacrifice this creature: It deals damage equal to its power …" — the source is gone by the
 * time the ability resolves, so `EntityProperty(Source, Power)` (`DynamicAmounts.sourcePower()`)
 * must read the power the source *last had on the battlefield*, not zero. `ActivateAbilityHandler`
 * snapshots the source's projected P/T at cost-payment time (mirroring the
 * `LastKnownSourceCounters` snapshot) and `DynamicAmountEvaluator` reads it back.
 *
 * Proven here with Ghitu Fire-Eater (2/2), an already-registered card that shares this latent gap
 * with Cinder Shade and Blazing Bomb's Blow Up. Before the fix the ability dealt 0 damage.
 */
class SelfSacrificeSourcePowerScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("Ghitu Fire-Eater: {T}, Sacrifice: deals damage equal to its power (2), not zero") {
        val d = driver()
        val me = d.player1
        val foe = d.player2

        val fireEater = d.putCreatureOnBattlefield(me, "Ghitu Fire-Eater") // 2/2
        d.removeSummoningSickness(fireEater)

        val abilityId = d.cardRegistry.requireCard("Ghitu Fire-Eater").activatedAbilities[0].id

        d.getLifeTotal(foe) shouldBe 20

        // Activate "{T}, Sacrifice this creature: It deals damage equal to its power to any target",
        // aimed at the opponent. The sacrifice removes the source before the effect resolves.
        d.submitSuccess(
            ActivateAbility(
                playerId = me,
                sourceId = fireEater,
                abilityId = abilityId,
                targets = listOf(ChosenTarget.Player(foe))
            )
        )

        // Resolve the ability off the stack.
        var guard = 0
        while (d.state.stack.isNotEmpty() && guard < 20) {
            d.bothPass()
            guard++
        }

        // It was sacrificed to pay the cost…
        d.getGraveyard(me).any { d.getCardName(it) == "Ghitu Fire-Eater" } shouldBe true
        // …and dealt 2 damage (its last-known power) — NOT 0 as before the LKI snapshot fix.
        d.getLifeTotal(foe) shouldBe 18
    }
})
