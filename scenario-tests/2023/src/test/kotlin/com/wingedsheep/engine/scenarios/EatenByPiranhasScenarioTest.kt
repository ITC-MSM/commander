package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.EatenByPiranhas
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for Eaten by Piranhas (LCI #54)
 * {1}{U} — Enchantment — Aura
 *
 * Flash
 * Enchant creature
 * Enchanted creature loses all abilities and is a black Skeleton creature with base power
 * and toughness 1/1. (It loses all other colors, card types, and creature types.)
 */
class EatenByPiranhasScenarioTest : FunSpec({

    // A 3/3 red creature with flying — used as the enchantment target
    val FlyingDragon = CardDefinition.creature(
        name = "Flying Dragon",
        manaCost = ManaCost.parse("{2}{R}"),
        subtypes = setOf(Subtype("Dragon")),
        power = 3,
        toughness = 3,
        keywords = setOf(Keyword.FLYING)
    )

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(FlyingDragon, EatenByPiranhas))
        return driver
    }

    test("Eaten by Piranhas sets enchanted creature's base power and toughness to 1/1") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Mountain" to 20))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val creature = driver.putCreatureOnBattlefield(activePlayer, "Flying Dragon")
        val aura = driver.putCardInHand(activePlayer, "Eaten by Piranhas")
        driver.giveMana(activePlayer, Color.BLUE, 1)
        driver.giveColorlessMana(activePlayer, 1)
        driver.castSpell(activePlayer, aura, listOf(creature))
        driver.bothPass()

        projector.getProjectedPower(driver.state, creature) shouldBe 1
        projector.getProjectedToughness(driver.state, creature) shouldBe 1
    }

    test("Eaten by Piranhas causes enchanted creature to lose all abilities") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Mountain" to 20))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val creature = driver.putCreatureOnBattlefield(activePlayer, "Flying Dragon")
        val aura = driver.putCardInHand(activePlayer, "Eaten by Piranhas")
        driver.giveMana(activePlayer, Color.BLUE, 1)
        driver.giveColorlessMana(activePlayer, 1)
        driver.castSpell(activePlayer, aura, listOf(creature))
        driver.bothPass()

        val projected = driver.state.projectedState
        projected.hasLostAllAbilities(creature) shouldBe true
        projected.hasKeyword(creature, Keyword.FLYING) shouldBe false
    }

    test("Eaten by Piranhas makes enchanted creature a black Skeleton, losing original color and creature types") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Mountain" to 20))

        val activePlayer = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val creature = driver.putCreatureOnBattlefield(activePlayer, "Flying Dragon")
        val aura = driver.putCardInHand(activePlayer, "Eaten by Piranhas")
        driver.giveMana(activePlayer, Color.BLUE, 1)
        driver.giveColorlessMana(activePlayer, 1)
        driver.castSpell(activePlayer, aura, listOf(creature))
        driver.bothPass()

        val projected = driver.state.projectedState
        // Gains Skeleton subtype, loses Dragon
        projected.hasSubtype(creature, "Skeleton") shouldBe true
        projected.hasSubtype(creature, "Dragon") shouldBe false
        // Becomes black, loses red
        projected.hasColor(creature, Color.BLACK) shouldBe true
        projected.hasColor(creature, Color.RED) shouldBe false
    }
})
