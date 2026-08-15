package com.wingedsheep.engine.legalactions

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.legalactions.support.setupP1
import com.wingedsheep.mtg.sets.definitions.blc.cards.BrightcapBadger
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

/**
 * An Adventure / Omen / modal-DFC card should surface BOTH faces to the drag-to-play menu
 * even when only one face is affordable: the unaffordable face is emitted with
 * `affordable = false` (a grayed-out placeholder) so the client shows both options and the
 * player can choose deliberately or cancel — rather than the menu silently auto-firing the
 * single enumerated face.
 *
 * When *neither* face is affordable, nothing is emitted (the card stays unplayable), matching
 * the contract for ordinary single-faced spells.
 *
 * Brightcap Badger // Fungus Frolic — creature face {3}{G}, Adventure (Instant) face {2}{G}.
 */
class AdventureFaceEnumerationTest : FunSpec({

    test("both faces affordable: two affordable cast actions, one per face") {
        val driver = setupP1(
            hand = listOf("Brightcap Badger"),
            battlefield = listOf("Forest", "Forest", "Forest", "Forest"), // {3}{G} and {2}{G} both ok
            extraSetCards = listOf(BrightcapBadger),
        )

        val casts = driver.enumerateFor(driver.player1).castActionsFor("Brightcap Badger")

        casts shouldHaveSize 2
        casts.all { it.affordable } shouldBe true
        casts.map { (it.action as CastSpell).faceIndex }.toSet() shouldBe setOf(null, 0)
    }

    test("only the cheaper Adventure face affordable: creature face surfaces as a grayed-out placeholder") {
        val driver = setupP1(
            hand = listOf("Brightcap Badger"),
            battlefield = listOf("Forest", "Forest", "Forest"), // {2}{G} adventure yes, {3}{G} creature no
            extraSetCards = listOf(BrightcapBadger),
        )

        val casts = driver.enumerateFor(driver.player1).castActionsFor("Brightcap Badger")

        casts shouldHaveSize 2
        val adventure = casts.single { (it.action as CastSpell).faceIndex == 0 }
        val creature = casts.single { (it.action as CastSpell).faceIndex == null }
        adventure.affordable shouldBe true
        creature.affordable shouldBe false
        // The grayed-out placeholder still carries the creature face's cost for display.
        creature.manaCostString shouldBe "{3}{G}"
    }

    test("neither face affordable: the card is not enumerated at all") {
        val driver = setupP1(
            hand = listOf("Brightcap Badger"),
            battlefield = listOf("Forest", "Forest"), // 2 mana: neither {2}{G} nor {3}{G}
            extraSetCards = listOf(BrightcapBadger),
        )

        driver.enumerateFor(driver.player1).castActionsFor("Brightcap Badger").shouldBeEmpty()
    }

    // The reverse direction: a (synthetic) adventurer whose creature face is cheaper than its
    // Adventure face. When only the creature face is affordable, the unaffordable Adventure face
    // must still surface as a grayed-out placeholder.
    val cheapBodyAdventure = card("Test Sprout") {
        manaCost = "{G}"
        typeLine = "Creature — Elf"
        power = 1
        toughness = 1
        adventure("Overgrow") {
            manaCost = "{4}{G}"
            typeLine = "Sorcery — Adventure"
            spell { effect = Effects.DrawCards(1) }
        }
    }

    // An {X} in the *Adventure face's* own mana cost. The face's cast action has to carry the X
    // fields, or the client never opens the X picker and the face casts at X = 0.
    val xCostAdventure = card("Test Thane") {
        manaCost = "{2}{W}{W}"
        typeLine = "Enchantment"
        adventure("Test Muster") {
            manaCost = "{X}{2}{W}"
            typeLine = "Sorcery — Adventure"
            spell {
                effect = Effects.CreateToken(
                    count = DynamicAmount.XValue, power = 2, toughness = 2, creatureTypes = setOf("Dwarf")
                )
            }
        }
    }

    test("an {X} in the Adventure face's cost is offered as an X cost, with a board-derived ceiling") {
        val driver = setupP1(
            hand = listOf("Test Thane"),
            battlefield = List(6) { "Plains" },
            extraSetCards = listOf(xCostAdventure),
        )

        val adventure = driver.enumerateFor(driver.player1).castActionsFor("Test Thane")
            .single { (it.action as CastSpell).faceIndex == 0 }

        adventure.hasXCost shouldBe true
        // Six lands, {X}{2}{W}: three mana is spoken for by the fixed part.
        adventure.maxAffordableX shouldBe 3
    }

    test("the primary face of the same card keeps its own (X-free) cost") {
        val driver = setupP1(
            hand = listOf("Test Thane"),
            battlefield = List(6) { "Plains" },
            extraSetCards = listOf(xCostAdventure),
        )

        val primary = driver.enumerateFor(driver.player1).castActionsFor("Test Thane")
            .single { (it.action as CastSpell).faceIndex == null }

        primary.hasXCost shouldBe false
        primary.maxAffordableX shouldBe null
    }

    test("only the creature face affordable: Adventure face surfaces as a grayed-out placeholder") {
        val driver = setupP1(
            hand = listOf("Test Sprout"),
            battlefield = listOf("Forest"), // {G} creature yes, {4}{G} adventure no
            extraSetCards = listOf(cheapBodyAdventure),
        )

        val casts = driver.enumerateFor(driver.player1).castActionsFor("Test Sprout")

        casts shouldHaveSize 2
        val creature = casts.single { (it.action as CastSpell).faceIndex == null }
        val adventure = casts.single { (it.action as CastSpell).faceIndex == 0 }
        creature.affordable shouldBe true
        adventure.affordable shouldBe false
        adventure.manaCostString shouldBe "{4}{G}"
    }
})
