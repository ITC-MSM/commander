package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.ParadigmComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.ImprovisationCapstone
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Improvisation Capstone (SOS) — {5}{R}{R} Sorcery — Lesson.
 *
 * "Exile cards from the top of your library until you exile cards with total mana value 4 or
 *  greater. You may cast any number of spells from among them without paying their mana costs.
 *  Paradigm (...)"
 *
 * Pins: exile stops once total exiled mana value reaches 4 (two MV-2 cards → exactly 2 exiled),
 * the spell self-exiles via Paradigm.
 */
class ImprovisationCapstoneScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + ImprovisationCapstone)
        return driver
    }

    test("exiles from top until total mana value reaches 4") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Stack the top of library with two mana-value-2 creatures (Black Creature = {1}{B} → MV 2).
        // 2 + 2 = 4 → exile stops after exactly two cards.
        driver.putCardOnTopOfLibrary(player, "Black Creature")
        driver.putCardOnTopOfLibrary(player, "Black Creature")

        val spell = driver.putCardInHand(player, "Improvisation Capstone")
        driver.giveColorlessMana(player, 5)
        driver.giveMana(player, Color.RED, 2)

        val exileBefore = driver.getExile(player).size
        driver.castSpell(player, spell).isSuccess shouldBe true
        driver.bothPass()

        val nowExiled = driver.getExile(player)
            .mapNotNull { driver.state.getEntity(it)?.get<CardComponent>()?.name }
            .count { it == "Black Creature" }
        nowExiled shouldBe 2

        // The Capstone is a Paradigm card → it self-exiles with the marker (not in graveyard).
        val capstoneExiled = driver.state.getZone(player, Zone.EXILE)
            .mapNotNull { driver.state.getEntity(it) }
            .filter { it.get<CardComponent>()?.name == "Improvisation Capstone" }
        capstoneExiled.any { it.get<ParadigmComponent>() != null } shouldBe true

        // Sanity: at least the two stacked cards were exiled.
        (driver.getExile(player).size - exileBefore) shouldBe (2 + 1) // 2 cards + the Paradigm spell
    }
})
