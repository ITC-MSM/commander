package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.scg.cards.LethalVapors
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for Lethal Vapors:
 * {2}{B}{B} Enchantment
 * Whenever a creature enters, destroy it.
 * {0}: Destroy Lethal Vapors. You skip your next turn. Any player may activate this ability.
 */
class LethalVaporsTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        return driver
    }

    test("destroys creatures when they enter the battlefield") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 20, "Forest" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(activePlayer, "Lethal Vapors")

        // Cast a creature — trigger should destroy it
        val bear = driver.putCardInHand(activePlayer, "Grizzly Bears")
        driver.giveMana(activePlayer, Color.GREEN, 2)
        driver.castSpell(activePlayer, bear)
        driver.bothPass() // resolve creature spell

        // Trigger fires: destroy the creature
        driver.bothPass()

        // Grizzly Bears should be destroyed
        driver.findPermanent(activePlayer, "Grizzly Bears") shouldBe null
    }

    test("destroys opponent creatures too") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 20, "Forest" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(activePlayer, "Lethal Vapors")

        // Opponent plays a creature
        driver.passPriority(activePlayer)
        val bear = driver.putCardInHand(opponent, "Grizzly Bears")
        driver.giveMana(opponent, Color.GREEN, 2)
        driver.castSpell(opponent, bear)
        driver.bothPass() // resolve creature

        // Trigger fires
        driver.bothPass()

        driver.findPermanent(opponent, "Grizzly Bears") shouldBe null
    }

    test("activated ability destroys Lethal Vapors") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 20, "Forest" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val vapors = driver.putPermanentOnBattlefield(activePlayer, "Lethal Vapors")

        // Activate {0}: Destroy Lethal Vapors
        val abilityId = LethalVapors.activatedAbilities[0].id
        driver.submit(
            ActivateAbility(
                playerId = activePlayer,
                sourceId = vapors,
                abilityId = abilityId
            )
        )
        driver.bothPass()

        // Lethal Vapors should be destroyed
        driver.findPermanent(activePlayer, "Lethal Vapors") shouldBe null

        // Now creatures can enter safely
        val bear = driver.putCardInHand(activePlayer, "Grizzly Bears")
        driver.giveMana(activePlayer, Color.GREEN, 2)
        driver.castSpell(activePlayer, bear)
        driver.bothPass()

        driver.findPermanent(activePlayer, "Grizzly Bears") shouldNotBe null
    }
})
