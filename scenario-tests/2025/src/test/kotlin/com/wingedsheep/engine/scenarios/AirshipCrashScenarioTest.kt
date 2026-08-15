package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CycleCard
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.AirshipCrash
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for Airship Crash.
 *
 * Airship Crash: {2}{G}
 * Instant
 * Destroy target artifact, enchantment, or creature with flying.
 * Cycling {2}
 */
class AirshipCrashScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + AirshipCrash)
        return driver
    }

    test("Airship Crash destroys a creature with flying") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Birds of Paradise has flying.
        val flyer = driver.putCreatureOnBattlefield(opponent, "Birds of Paradise")

        val crash = driver.putCardInHand(activePlayer, "Airship Crash")
        driver.giveMana(activePlayer, Color.GREEN, 3)

        val castResult = driver.castSpell(activePlayer, crash, listOf(flyer))
        castResult.isSuccess shouldBe true
        driver.bothPass()

        driver.findPermanent(opponent, "Birds of Paradise") shouldBe null
        driver.getGraveyardCardNames(opponent) shouldContain "Birds of Paradise"
    }

    test("Airship Crash cannot target a creature without flying") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val grounded = driver.putCreatureOnBattlefield(opponent, "Centaur Courser")

        val crash = driver.putCardInHand(activePlayer, "Airship Crash")
        driver.giveMana(activePlayer, Color.GREEN, 3)

        val castResult = driver.castSpell(activePlayer, crash, listOf(grounded))
        castResult.isSuccess shouldBe false

        driver.findPermanent(opponent, "Centaur Courser") shouldNotBe null
    }

    test("Airship Crash can be cycled") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val crash = driver.putCardInHand(activePlayer, "Airship Crash")
        driver.giveMana(activePlayer, Color.GREEN, 2)

        val handBefore = driver.getHandSize(activePlayer)
        val result = driver.submit(CycleCard(playerId = activePlayer, cardId = crash))
        result.isSuccess shouldBe true
        driver.bothPass()

        // Cycling discards Airship Crash and draws a card; net hand size unchanged.
        driver.getGraveyardCardNames(activePlayer) shouldContain "Airship Crash"
        driver.getHandSize(activePlayer) shouldBe handBefore
    }
})
