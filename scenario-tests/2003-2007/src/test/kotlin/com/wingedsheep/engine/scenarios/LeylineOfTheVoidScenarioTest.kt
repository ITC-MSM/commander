package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.gpt.cards.LeylineOfTheVoid
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Leyline of the Void (Guildpact #52):
 *   "If this card is in your opening hand, you may begin the game with it on the battlefield.
 *    If a card would be put into an opponent's graveyard from anywhere, exile it instead."
 *
 * The replacement is [com.wingedsheep.sdk.scripting.RedirectZoneChange] graveyard -> exile scoped
 * to `ownedByOpponent()`. These tests prove the asymmetry that defines the card: a card that would
 * head to an *opponent's* graveyard is exiled instead, while a card going to the *controller's own*
 * graveyard is untouched.
 */
class LeylineOfTheVoidScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCards(listOf(LeylineOfTheVoid))
        return driver
    }

    fun startTurn(driver: GameTestDriver): EntityId {
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver.activePlayer!!
    }

    test("a card headed to an opponent's graveyard is exiled instead") {
        val driver = createDriver()
        val you = startTurn(driver)
        val opp = driver.getOpponent(you)

        // You control Leyline of the Void; the opponent owns a creature about to die.
        driver.putPermanentOnBattlefield(you, "Leyline of the Void")
        val victim = driver.putCreatureOnBattlefield(opp, "Savannah Lions") // 1/1

        val bolt = driver.putCardInHand(you, "Lightning Bolt")
        driver.giveMana(you, Color.RED, 1)
        driver.castSpell(you, bolt, targets = listOf(victim)).isSuccess shouldBe true
        driver.bothPass() // Bolt resolves, deals 3 to the 1/1 — it would die into the opp's graveyard

        driver.getGraveyard(opp) shouldNotContain victim
        driver.getExile(opp) shouldContain victim
    }

    test("a card headed to the controller's own graveyard is NOT affected") {
        val driver = createDriver()
        val you = startTurn(driver)

        // You control Leyline of the Void; your own instant resolves and heads to your graveyard.
        driver.putPermanentOnBattlefield(you, "Leyline of the Void")
        val bolt = driver.putCardInHand(you, "Lightning Bolt")
        driver.giveMana(you, Color.RED, 1)
        driver.castSpell(you, bolt, targets = listOf(you)).isSuccess shouldBe true
        driver.bothPass() // Bolt resolves and heads to its owner's (your) graveyard

        // Your own card lands in your graveyard normally — Leyline only hits opponents.
        driver.getGraveyard(you) shouldContain bolt
        driver.getExile(you) shouldNotContain bolt
    }
})
