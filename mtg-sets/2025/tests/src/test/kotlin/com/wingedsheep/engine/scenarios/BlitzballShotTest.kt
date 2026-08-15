package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.BlitzballShot
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Blitzball Shot — {1}{G} Instant
 * "Target creature gets +3/+3 and gains trample until end of turn."
 */
class BlitzballShotTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(BlitzballShot)
        return driver
    }

    test("grants +3/+3 and trample until end of turn") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 30), startingLife = 20)

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val lions = driver.putCreatureOnBattlefield(activePlayer, "Savannah Lions") // 1/1
        projector.hasProjectedKeyword(driver.state, lions, Keyword.TRAMPLE) shouldBe false

        val shot = driver.putCardInHand(activePlayer, "Blitzball Shot")
        driver.giveMana(activePlayer, Color.GREEN, 1)
        driver.giveColorlessMana(activePlayer, 1)
        driver.castSpell(activePlayer, shot, targets = listOf(lions))
        driver.bothPass()

        projector.getProjectedPower(driver.state, lions) shouldBe 4
        projector.getProjectedToughness(driver.state, lions) shouldBe 4
        projector.hasProjectedKeyword(driver.state, lions, Keyword.TRAMPLE) shouldBe true
    }
})
