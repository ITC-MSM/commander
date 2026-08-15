package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.ktk.KhansOfTarkirSet
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe

/**
 * Tests for Kheru Bloodsucker.
 *
 * Kheru Bloodsucker
 * {2}{B}
 * Creature — Vampire
 * 2/2
 * Whenever a creature you control with toughness 4 or greater dies, each opponent loses 2 life
 * and you gain 2 life.
 * {2}{B}, Sacrifice another creature: Put a +1/+1 counter on Kheru Bloodsucker.
 */
class KheruBloodsuckerTest : FunSpec({

    val greenBrute = CardDefinition.creature(
        name = "Green Brute",
        manaCost = ManaCost.parse("{3}{G}"),
        subtypes = setOf(Subtype("Beast")),
        power = 4,
        toughness = 4
    )

    val smallCreature = CardDefinition.creature(
        name = "Small Creature",
        manaCost = ManaCost.parse("{1}{G}"),
        subtypes = setOf(Subtype("Beast")),
        power = 2,
        toughness = 2
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + KhansOfTarkirSet.cards)
        driver.registerCard(greenBrute)
        driver.registerCard(smallCreature)
        return driver
    }

    test("trigger fires when a 4/4 creature you control dies - opponent loses 2 life and you gain 2 life") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 20, "Forest" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Put Kheru Bloodsucker and a 4/4 creature on the battlefield
        driver.putCreatureOnBattlefield(activePlayer, "Kheru Bloodsucker")
        val brute = driver.putCreatureOnBattlefield(activePlayer, "Green Brute")

        // Verify starting life
        driver.getLifeTotal(activePlayer) shouldBe 20
        driver.getLifeTotal(opponent) shouldBe 20

        // Use Doom Blade to destroy the 4/4 (it's green, so nonblack)
        driver.giveMana(activePlayer, Color.BLACK, 2)
        val doomBlade = driver.putCardInHand(activePlayer, "Doom Blade")
        val castResult = driver.castSpellWithTargets(activePlayer, doomBlade, listOf(ChosenTarget.Permanent(brute)))
        castResult.isSuccess shouldBe true

        // Resolve Doom Blade
        driver.bothPass()

        // The 4/4 should be dead
        driver.findPermanent(activePlayer, "Green Brute") shouldBe null

        // Kheru Bloodsucker's death trigger should be on the stack
        driver.stackSize shouldBeGreaterThan 0

        // Resolve the death trigger
        driver.bothPass()

        // Active player should have gained 2 life (20 + 2 = 22)
        driver.getLifeTotal(activePlayer) shouldBe 22
        // Opponent should have lost 2 life (20 - 2 = 18)
        driver.getLifeTotal(opponent) shouldBe 18
    }

    test("trigger does NOT fire when a creature with toughness less than 4 dies") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Swamp" to 20, "Forest" to 20),
            startingLife = 20
        )

        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Put Kheru Bloodsucker and a 2/2 creature on the battlefield
        driver.putCreatureOnBattlefield(activePlayer, "Kheru Bloodsucker")
        val small = driver.putCreatureOnBattlefield(activePlayer, "Small Creature")

        // Use Doom Blade to destroy the 2/2
        driver.giveMana(activePlayer, Color.BLACK, 2)
        val doomBlade = driver.putCardInHand(activePlayer, "Doom Blade")
        val castResult = driver.castSpellWithTargets(activePlayer, doomBlade, listOf(ChosenTarget.Permanent(small)))
        castResult.isSuccess shouldBe true

        // Resolve Doom Blade
        driver.bothPass()

        // The 2/2 should be dead
        driver.findPermanent(activePlayer, "Small Creature") shouldBe null

        // No trigger should fire - stack should be empty (no pending triggers)
        // Life totals should remain unchanged
        driver.getLifeTotal(activePlayer) shouldBe 20
        driver.getLifeTotal(opponent) shouldBe 20
    }
})
