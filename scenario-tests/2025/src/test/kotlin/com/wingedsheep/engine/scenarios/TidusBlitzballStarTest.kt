package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.TidusBlitzballStar
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tidus, Blitzball Star — {1}{W}{U} 2/1 Legendary Creature
 * "Whenever Tidus attacks, tap target creature an opponent controls."
 */
class TidusBlitzballStarTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(TidusBlitzballStar)
        return driver
    }

    test("taps a target creature an opponent controls when attacking") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 30), startingLife = 20)

        val player = driver.activePlayer!!
        val opponent = driver.getOpponent(player)

        val tidus = driver.putCreatureOnBattlefield(player, "Tidus, Blitzball Star")
        driver.removeSummoningSickness(tidus)
        val blocker = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")
        driver.isTapped(blocker) shouldBe false

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(player, listOf(tidus), opponent)

        // Attack trigger needs a target.
        driver.submitTargetSelection(player, listOf(blocker))
        driver.bothPass()

        driver.isTapped(blocker) shouldBe true
    }
})
