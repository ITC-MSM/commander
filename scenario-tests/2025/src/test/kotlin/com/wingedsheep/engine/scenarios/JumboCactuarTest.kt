package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.JumboCactuar
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Jumbo Cactuar — {5}{G}{G} 1/7 Creature
 * "10,000 Needles — Whenever this creature attacks, it gets +9999/+0 until end of turn."
 */
class JumboCactuarTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(JumboCactuar)
        return driver
    }

    test("gets +9999/+0 when it attacks") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 30), startingLife = 20)

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)

        val cactuar = driver.putCreatureOnBattlefield(activePlayer, "Jumbo Cactuar")
        driver.removeSummoningSickness(cactuar)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(activePlayer, listOf(cactuar), opponent)
        driver.bothPass() // resolve the attack trigger

        // Base power 1 + 9999.
        projector.getProjectedPower(driver.state, cactuar) shouldBe 10000
        projector.getProjectedToughness(driver.state, cactuar) shouldBe 7
    }
})
