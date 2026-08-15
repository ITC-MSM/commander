package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.ParadigmComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.sos.cards.DecorumDissertation
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Decorum Dissertation (SOS) — {3}{B}{B} Sorcery — Lesson.
 *
 * "Target player draws two cards and loses 2 life. Paradigm (...)"
 *
 * Pins: the chosen player both draws 2 and loses 2 (same target); the resolved spell exiles itself
 * with the Paradigm marker rather than going to the graveyard.
 */
class DecorumDissertationScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + DecorumDissertation)
        return driver
    }

    test("target player draws two and loses two; spell self-exiles via Paradigm") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))
        val player = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val handBefore = driver.getHandSize(player)
        val lifeBefore = driver.getLifeTotal(player)

        val spell = driver.putCardInHand(player, "Decorum Dissertation")
        driver.giveColorlessMana(player, 3)
        driver.giveMana(player, Color.BLACK, 2)
        driver.castSpell(player, spell, targets = listOf(player)).isSuccess shouldBe true
        driver.bothPass()

        // handBefore was measured before adding the spell to hand; the spell leaves hand on cast,
        // then the player draws 2 → net +2 vs handBefore.
        driver.getHandSize(player) shouldBe handBefore + 2
        driver.getLifeTotal(player) shouldBe lifeBefore - 2

        // Paradigm: the resolved spell is in exile with the marker, not in the graveyard.
        val exiled = driver.state.getZone(player, Zone.EXILE)
            .mapNotNull { driver.state.getEntity(it) }
            .filter { it.get<CardComponent>()?.name == "Decorum Dissertation" }
        exiled.any { it.get<ParadigmComponent>() != null } shouldBe true
    }
})
