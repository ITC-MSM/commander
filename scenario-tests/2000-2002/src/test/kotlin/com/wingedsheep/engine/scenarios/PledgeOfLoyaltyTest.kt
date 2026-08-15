package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.inv.cards.PledgeOfLoyalty
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Pledge of Loyalty (Invasion engine gap #15 — dynamic multi-color protection from a
 * board-computed color set).
 *
 * Pledge of Loyalty: {1}{W}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature has protection from the colors of permanents you control.
 *
 * The protection set is derived from the colors of permanents the *Aura's controller* controls,
 * recomputed at projection — so enchanting an opponent's creature proves "you" is the Aura
 * controller (not the enchanted creature's controller), and adding a new colored permanent updates
 * the protection set dynamically.
 */
class PledgeOfLoyaltyTest : FunSpec({

    fun coloredBear(name: String, mana: String) = CardDefinition.creature(
        name = name,
        manaCost = ManaCost.parse(mana),
        subtypes = setOf(Subtype("Bear")),
        power = 2,
        toughness = 2
    )

    val BlueBear = coloredBear("Blue Bear", "{U}")
    val RedBear = coloredBear("Red Bear", "{R}")
    val BlackBear = coloredBear("Black Bear", "{B}")

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + listOf(PledgeOfLoyalty, BlueBear, RedBear, BlackBear)
        )
        return driver
    }

    test("Enchanted creature gains protection from the colors of the Aura controller's permanents") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Forest" to 20),
            startingLife = 20
        )

        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // I control a blue creature; the Pledge itself (white) will also be mine.
        driver.putCreatureOnBattlefield(me, "Blue Bear")
        // The opponent controls the red creature I'm going to enchant.
        val opponentRedBear = driver.putCreatureOnBattlefield(opponent, "Red Bear")

        val pledge = driver.putCardInHand(me, "Pledge of Loyalty")
        driver.giveMana(me, Color.WHITE, 2)
        driver.castSpell(me, pledge, listOf(opponentRedBear))
        driver.bothPass()

        val projected = projector.project(driver.state)
        // From my permanents: the white Pledge + my blue Bear.
        projected.hasKeyword(opponentRedBear, "PROTECTION_FROM_WHITE") shouldBe true
        projected.hasKeyword(opponentRedBear, "PROTECTION_FROM_BLUE") shouldBe true
        // The enchanted creature's own red color is NOT mine, so no protection from red...
        projected.hasKeyword(opponentRedBear, "PROTECTION_FROM_RED") shouldBe false
        // ...and colors no one controls are absent.
        projected.hasKeyword(opponentRedBear, "PROTECTION_FROM_BLACK") shouldBe false
        projected.hasKeyword(opponentRedBear, "PROTECTION_FROM_GREEN") shouldBe false
    }

    test("Protection set updates when the controller gains a new colored permanent") {
        val driver = createDriver()
        driver.initMirrorMatch(
            deck = Deck.of("Plains" to 20, "Forest" to 20),
            startingLife = 20
        )

        val me = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Enchant my own blue creature.
        val myBlueBear = driver.putCreatureOnBattlefield(me, "Blue Bear")
        val pledge = driver.putCardInHand(me, "Pledge of Loyalty")
        driver.giveMana(me, Color.WHITE, 2)
        driver.castSpell(me, pledge, listOf(myBlueBear))
        driver.bothPass()

        // Initially: white (Pledge) + blue (the enchanted creature itself, which I control).
        val before = projector.project(driver.state)
        before.hasKeyword(myBlueBear, "PROTECTION_FROM_WHITE") shouldBe true
        before.hasKeyword(myBlueBear, "PROTECTION_FROM_BLUE") shouldBe true
        before.hasKeyword(myBlueBear, "PROTECTION_FROM_BLACK") shouldBe false

        // Add a black permanent under my control — the board-derived set must pick it up.
        driver.putCreatureOnBattlefield(me, "Black Bear")
        val after = projector.project(driver.state)
        after.hasKeyword(myBlueBear, "PROTECTION_FROM_BLACK") shouldBe true
        after.hasKeyword(myBlueBear, "PROTECTION_FROM_WHITE") shouldBe true
        after.hasKeyword(myBlueBear, "PROTECTION_FROM_BLUE") shouldBe true
    }
})
