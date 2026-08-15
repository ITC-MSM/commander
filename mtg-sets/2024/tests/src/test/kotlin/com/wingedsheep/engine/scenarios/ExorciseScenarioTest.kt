package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.Exorcise
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Exorcise — {1}{W} Sorcery
 * Exile target artifact, enchantment, or creature with power 4 or greater.
 *
 * The power restriction binds only to the creature branch: any artifact/enchantment is a
 * legal target regardless of power, but a creature must have power 4+.
 */
class ExorciseScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(Exorcise)
        return driver
    }

    test("exiles a creature with power 4 or greater") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        // Force of Nature is a 5/5 — power 4+.
        val bigCreature = driver.putCreatureOnBattlefield(opponent, "Force of Nature")
        val exorcise = driver.putCardInHand(me, "Exorcise")

        driver.giveMana(me, Color.WHITE, 1)
        driver.giveColorlessMana(me, 1)
        driver.castSpell(me, exorcise, listOf(bigCreature)).isSuccess shouldBe true
        driver.bothPass()
        driver.isPaused shouldBe false

        driver.getExileCardNames(opponent) shouldContain "Force of Nature"
        driver.findPermanent(opponent, "Force of Nature") shouldBe null
    }

    test("exiles an enchantment regardless of power") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        val enchantment = driver.putPermanentOnBattlefield(opponent, "Test Enchantment")
        val exorcise = driver.putCardInHand(me, "Exorcise")

        driver.giveMana(me, Color.WHITE, 1)
        driver.giveColorlessMana(me, 1)
        driver.castSpell(me, exorcise, listOf(enchantment)).isSuccess shouldBe true
        driver.bothPass()
        driver.isPaused shouldBe false

        driver.getExileCardNames(opponent) shouldContain "Test Enchantment"
    }

    test("cannot target a creature with power less than 4") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)

        // Savannah Lions is a 2/1 — not a legal target.
        val smallCreature = driver.putCreatureOnBattlefield(opponent, "Savannah Lions")
        val exorcise = driver.putCardInHand(me, "Exorcise")

        driver.giveMana(me, Color.WHITE, 1)
        driver.giveColorlessMana(me, 1)
        driver.castSpell(me, exorcise, listOf(smallCreature)).isSuccess shouldBe false
        driver.getExileCardNames(opponent) shouldNotContain "Savannah Lions"
    }
})
