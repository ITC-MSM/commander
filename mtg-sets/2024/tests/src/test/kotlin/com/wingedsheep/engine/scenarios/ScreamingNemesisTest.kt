package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.CardScript
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.GainLifeEffect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Screaming Nemesis (DSK #157) — proves the durable, source-independent life-gain lock
 * ([com.wingedsheep.sdk.scripting.effects.LockLifeGainEffect]).
 *
 * "Whenever this creature is dealt damage, it deals that much damage to any other target. If a
 * player is dealt damage this way, they can't gain life for the rest of the game."
 *
 * The reflected damage is the existing Tephraderm/any-target composition; the new engine surface is
 * the rest-of-game can't-gain-life lock applied to a struck player.
 */
class ScreamingNemesisTest : FunSpec({

    // "You gain 5 life." — used to probe whether a player's life gain is locked.
    val GainFiveLife = CardDefinition.instant(
        name = "Gain Five Life",
        manaCost = ManaCost.parse("{W}"),
        oracleText = "You gain 5 life.",
        script = CardScript.spell(effect = GainLifeEffect(5)),
    )

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(GainFiveLife))
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    /** Resolve the stack, answering the reflected-damage trigger's target choice with [reflectTarget]. */
    fun resolveStack(driver: GameTestDriver, controller: EntityId, reflectTarget: EntityId) {
        var guard = 0
        while (guard++ < 16) {
            when (driver.state.pendingDecision) {
                is ChooseTargetsDecision -> driver.submitTargetSelection(controller, listOf(reflectTarget))
                null -> {
                    if (driver.state.stack.isEmpty()) return
                    driver.bothPass()
                }
                else -> driver.autoResolveDecision()
            }
        }
    }

    test("a player struck by the reflected damage can't gain life for the rest of the game") {
        val driver = newDriver()
        val p1 = driver.player1
        val p2 = driver.player2

        val nemesis = driver.putCreatureOnBattlefield(p1, "Screaming Nemesis")

        // Shock the Nemesis for 2 (survives, 3/3); it reflects 2 to a chosen target — pick p2.
        driver.giveMana(p1, Color.RED, 1)
        val shock = driver.putCardInHand(p1, "Shock")
        driver.castSpellWithTargets(p1, shock, listOf(ChosenTarget.Permanent(nemesis))).error shouldBe null
        resolveStack(driver, controller = p1, reflectTarget = p2)

        driver.getLifeTotal(p2) shouldBe 18 // 20 - 2 reflected

        // On p2's own turn, a gain-life spell does nothing — the lock persists across turns.
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val gain = driver.putCardInHand(p2, "Gain Five Life")
        driver.giveMana(p2, Color.WHITE, 1)
        driver.castSpell(p2, gain).error shouldBe null
        driver.bothPass()

        driver.getLifeTotal(p2) shouldBe 18 // no life gained
    }

    test("reflecting onto a creature locks no one — the targeted creature simply takes the damage") {
        val driver = newDriver()
        val p1 = driver.player1
        val p2 = driver.player2

        val nemesis = driver.putCreatureOnBattlefield(p1, "Screaming Nemesis")
        val turtle = driver.putCreatureOnBattlefield(p2, "Horned Turtle") // 1/4, survives 2

        driver.giveMana(p1, Color.RED, 1)
        val shock = driver.putCardInHand(p1, "Shock")
        driver.castSpellWithTargets(p1, shock, listOf(ChosenTarget.Permanent(nemesis))).error shouldBe null
        resolveStack(driver, controller = p1, reflectTarget = turtle)

        // p2 was not the damage recipient, so they can still gain life normally.
        driver.passPriorityUntil(Step.UPKEEP)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val gain = driver.putCardInHand(p2, "Gain Five Life")
        driver.giveMana(p2, Color.WHITE, 1)
        driver.castSpell(p2, gain).error shouldBe null
        driver.bothPass()

        driver.getLifeTotal(p2) shouldBe 25 // 20 + 5
    }
})
