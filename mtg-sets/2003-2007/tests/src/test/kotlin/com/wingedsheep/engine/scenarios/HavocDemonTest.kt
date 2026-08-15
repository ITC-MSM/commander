package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lgn.cards.HavocDemon
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for Havoc Demon:
 * {5}{B}{B} Creature — Demon 5/5
 * Flying
 * When Havoc Demon dies, all creatures get -5/-5 until end of turn.
 */
class HavocDemonTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(HavocDemon))
        return driver
    }

    test("dying Havoc Demon gives all creatures -5/-5") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Swamp" to 20))

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val demon = driver.putCreatureOnBattlefield(activePlayer, "Havoc Demon")
        val bears = driver.putCreatureOnBattlefield(activePlayer, "Grizzly Bears")
        val oppCreature = driver.putCreatureOnBattlefield(opponent, "Hill Giant")

        // Kill Havoc Demon with Lightning Bolt + enough damage
        // Havoc Demon is 5/5, so we need to deal 5 damage. Use two bolts.
        val bolt1 = driver.putCardInHand(activePlayer, "Lightning Bolt")
        val bolt2 = driver.putCardInHand(activePlayer, "Lightning Bolt")
        driver.giveMana(activePlayer, Color.RED, 2)

        // Cast first bolt targeting Havoc Demon
        driver.castSpell(activePlayer, bolt1, listOf(demon))
        driver.bothPass()

        // Cast second bolt targeting Havoc Demon (now 5/2)
        driver.castSpell(activePlayer, bolt2, listOf(demon))
        driver.bothPass()

        // Havoc Demon should be dead. Its trigger should be on the stack.
        driver.findPermanent(activePlayer, "Havoc Demon") shouldBe null
        driver.stackSize shouldBe 1

        // Resolve the death trigger
        driver.bothPass()

        // Grizzly Bears (2/2) gets -5/-5 = -3/-3, should die from SBA
        driver.findPermanent(activePlayer, "Grizzly Bears") shouldBe null

        // Hill Giant (3/3) gets -5/-5 = -2/-2, should also die
        driver.findPermanent(opponent, "Hill Giant") shouldBe null
    }

    test("creatures with enough toughness survive the -5/-5") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 20, "Swamp" to 20))

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val demon = driver.putCreatureOnBattlefield(activePlayer, "Havoc Demon")
        // Put a second Havoc Demon on opponent's side (5/5, survives at 0/0... no, dies)
        // We need something bigger. Let's use a creature with >5 toughness
        val oppDemon = driver.putCreatureOnBattlefield(opponent, "Havoc Demon")

        // Kill our demon
        val bolt1 = driver.putCardInHand(activePlayer, "Lightning Bolt")
        val bolt2 = driver.putCardInHand(activePlayer, "Lightning Bolt")
        driver.giveMana(activePlayer, Color.RED, 2)

        driver.castSpell(activePlayer, bolt1, listOf(demon))
        driver.bothPass()
        driver.castSpell(activePlayer, bolt2, listOf(demon))
        driver.bothPass()

        // Havoc Demon dies, trigger on stack
        driver.findPermanent(activePlayer, "Havoc Demon") shouldBe null
        driver.stackSize shouldBe 1

        // Resolve death trigger — opponent's Havoc Demon (5/5) gets -5/-5 = 0/0, dies
        driver.bothPass()

        // Opponent's demon should also die (0 toughness from SBA)
        driver.findPermanent(opponent, "Havoc Demon") shouldBe null
    }
})
