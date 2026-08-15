package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fdn.cards.FirespitterWhelp
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Firespitter Whelp — {2}{R} Creature — Dragon 2/2
 *
 * "Flying
 *  Whenever you cast a noncreature or Dragon spell, this creature deals 1 damage to each opponent."
 */
class FirespitterWhelpScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(FirespitterWhelp)
        return driver
    }

    test("casting a noncreature spell deals 1 damage to each opponent") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 30), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        driver.putCreatureOnBattlefield(me, "Firespitter Whelp")

        // A creature for the bolt to hit, so opponent life only reflects the Whelp trigger.
        val bears = driver.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        val bolt = driver.putCardInHand(me, "Lightning Bolt")
        driver.giveMana(me, Color.RED, 1)
        driver.castSpell(me, bolt, targets = listOf(bears))
        driver.bothPass() // resolve the Whelp's cast trigger (1 to each opponent)
        driver.bothPass() // resolve Lightning Bolt (3 to the Bears)

        driver.getLifeTotal(opponent) shouldBe 19
    }

    test("casting a Dragon creature spell also triggers the ability") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 30), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        driver.putCreatureOnBattlefield(me, "Firespitter Whelp")

        // Casting a second Firespitter Whelp is casting a Dragon spell → the first one triggers.
        val secondWhelp = driver.putCardInHand(me, "Firespitter Whelp")
        driver.giveMana(me, Color.RED, 1)
        driver.giveColorlessMana(me, 2)
        driver.castSpell(me, secondWhelp)
        driver.bothPass() // resolve the cast trigger (1 to each opponent)
        driver.bothPass() // resolve the Dragon spell

        driver.getLifeTotal(opponent) shouldBe 19
    }
})
