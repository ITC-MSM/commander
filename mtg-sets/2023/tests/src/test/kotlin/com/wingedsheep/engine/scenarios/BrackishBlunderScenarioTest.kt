package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.BrackishBlunder
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Brackish Blunder (LCI #46) — {1}{U} Instant
 *
 * "Return target creature to its owner's hand. If it was tapped, create a Map token."
 *
 * Proves two cases:
 *  1. A tapped creature is returned to its owner's hand and a Map token is created for the caster.
 *  2. An untapped creature is returned to its owner's hand but no Map token is created.
 *
 * The implementation checks [Conditions.TargetIsTapped] before the bounce (see card comment) so
 * the condition accurately captures the creature's pre-bounce tapped state.
 */
class BrackishBlunderScenarioTest : FunSpec({

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(PredefinedTokens.allTokens)
        driver.registerCard(BrackishBlunder)
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    test("bouncing a tapped creature returns it to hand and creates a Map token") {
        val driver = newDriver()
        val me = driver.player1
        val opp = driver.player2

        // Opponent has a tapped creature on the battlefield.
        val bear = driver.putCreatureOnBattlefield(opp, "Grizzly Bears")
        driver.tapPermanent(bear)
        val oppHandBefore = driver.getHandSize(opp)

        // Cast Brackish Blunder targeting the tapped creature.
        val spell = driver.putCardInHand(me, "Brackish Blunder")
        driver.giveColorlessMana(me, 1)
        driver.giveMana(me, Color.BLUE, 1)

        driver.castSpellWithTargets(me, spell, listOf(ChosenTarget.Permanent(bear))).isSuccess shouldBe true
        driver.bothPass()

        // Creature is returned to the opponent's hand.
        driver.findPermanent(opp, "Grizzly Bears") shouldBe null
        driver.getHandSize(opp) shouldBe oppHandBefore + 1

        // A Map token is created on the caster's battlefield because the creature was tapped.
        driver.findPermanent(me, "Map") shouldNotBe null
    }

    test("bouncing an untapped creature returns it to hand without creating a Map token") {
        val driver = newDriver()
        val me = driver.player1
        val opp = driver.player2

        // Opponent has an untapped creature on the battlefield.
        val bear = driver.putCreatureOnBattlefield(opp, "Grizzly Bears")
        // bear is untapped by default — no tapPermanent call.
        val oppHandBefore = driver.getHandSize(opp)

        // Cast Brackish Blunder targeting the untapped creature.
        val spell = driver.putCardInHand(me, "Brackish Blunder")
        driver.giveColorlessMana(me, 1)
        driver.giveMana(me, Color.BLUE, 1)

        driver.castSpellWithTargets(me, spell, listOf(ChosenTarget.Permanent(bear))).isSuccess shouldBe true
        driver.bothPass()

        // Creature is returned to the opponent's hand.
        driver.findPermanent(opp, "Grizzly Bears") shouldBe null
        driver.getHandSize(opp) shouldBe oppHandBefore + 1

        // No Map token because the creature was not tapped.
        driver.findPermanent(me, "Map") shouldBe null
    }
})
