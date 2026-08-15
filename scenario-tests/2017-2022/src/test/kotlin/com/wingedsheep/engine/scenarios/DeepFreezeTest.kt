package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dom.cards.DeepFreeze
import com.wingedsheep.sdk.core.*
import com.wingedsheep.sdk.model.*
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.costs.PayCost
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Deep Freeze
 * {2}{U}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature has base power and toughness 0/4, has defender, loses all other
 * abilities, and is a blue Wall in addition to its other colors and types.
 */
class DeepFreezeTest : FunSpec({

    // A 3/3 red creature with flying
    val FlyingCreature = CardDefinition.creature(
        name = "Flying Creature",
        manaCost = ManaCost.parse("{2}{R}"),
        subtypes = setOf(Subtype("Bird")),
        power = 3,
        toughness = 3,
        keywords = setOf(Keyword.FLYING)
    )

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(FlyingCreature, DeepFreeze))
        return driver
    }

    test("Deep Freeze sets base P/T to 0/4") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Mountain" to 20))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val creature = driver.putCreatureOnBattlefield(activePlayer, "Flying Creature")

        val deepFreeze = driver.putCardInHand(activePlayer, "Deep Freeze")
        driver.giveMana(activePlayer, Color.BLUE, 1)
        driver.giveColorlessMana(activePlayer, 2)
        driver.castSpell(activePlayer, deepFreeze, listOf(creature))
        driver.bothPass()

        projector.getProjectedPower(driver.state, creature) shouldBe 0
        projector.getProjectedToughness(driver.state, creature) shouldBe 4
    }

    test("Deep Freeze grants defender and removes flying") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Mountain" to 20))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val creature = driver.putCreatureOnBattlefield(activePlayer, "Flying Creature")

        val deepFreeze = driver.putCardInHand(activePlayer, "Deep Freeze")
        driver.giveMana(activePlayer, Color.BLUE, 1)
        driver.giveColorlessMana(activePlayer, 2)
        driver.castSpell(activePlayer, deepFreeze, listOf(creature))
        driver.bothPass()

        val projected = driver.state.projectedState
        projected.hasKeyword(creature, Keyword.DEFENDER) shouldBe true
        projected.hasKeyword(creature, Keyword.FLYING) shouldBe false
    }

    test("Deep Freeze adds Wall subtype and blue color") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Mountain" to 20))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val creature = driver.putCreatureOnBattlefield(activePlayer, "Flying Creature")

        val deepFreeze = driver.putCardInHand(activePlayer, "Deep Freeze")
        driver.giveMana(activePlayer, Color.BLUE, 1)
        driver.giveColorlessMana(activePlayer, 2)
        driver.castSpell(activePlayer, deepFreeze, listOf(creature))
        driver.bothPass()

        val projected = driver.state.projectedState
        projected.hasSubtype(creature, "Wall") shouldBe true
        projected.hasSubtype(creature, "Bird") shouldBe true // keeps original types
        projected.hasColor(creature, Color.BLUE) shouldBe true
    }

    test("Deep Freeze sets lostAllAbilities flag") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Mountain" to 20))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val creature = driver.putCreatureOnBattlefield(activePlayer, "Flying Creature")

        val deepFreeze = driver.putCardInHand(activePlayer, "Deep Freeze")
        driver.giveMana(activePlayer, Color.BLUE, 1)
        driver.giveColorlessMana(activePlayer, 2)
        driver.castSpell(activePlayer, deepFreeze, listOf(creature))
        driver.bothPass()

        val projected = driver.state.projectedState
        projected.hasLostAllAbilities(creature) shouldBe true
    }
})
