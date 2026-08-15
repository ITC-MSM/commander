package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CrewVehicle
import com.wingedsheep.engine.core.DeclareAttackers
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.TheLunarWhale
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for The Lunar Whale.
 *
 * The Lunar Whale: {3}{U}
 * Legendary Artifact — Vehicle
 * 3/5
 * Flying
 * You may look at the top card of your library any time.
 * As long as The Lunar Whale attacked this turn, you may play the top card of your library.
 * Crew 1
 *
 * The "play the top card" permission is a [com.wingedsheep.sdk.scripting.PlayLandsAndCastFilteredFromTopOfLibrary]
 * (unrestricted spell filter) gated by a [com.wingedsheep.sdk.scripting.ConditionalStaticAbility] on
 * "this attacked this turn". These tests exercise the gate: the permission is dormant until the
 * Whale is crewed and attacks, then becomes active for the rest of the turn.
 */
class TheLunarWhaleScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(TheLunarWhale)
        return driver
    }

    test("cannot play the top card of library before The Lunar Whale attacks") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Plains" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!

        // Whale present but it has not attacked this turn — the conditional gate is false.
        driver.putPermanentOnBattlefield(you, "The Lunar Whale")
        val forestOnTop = driver.putCardOnTopOfLibrary(you, "Forest")

        // A land on top of library can only be played via the (currently inactive) permission.
        driver.playLand(you, forestOnTop).isSuccess shouldBe false
        driver.findPermanent(you, "Forest") shouldBe null
    }

    test("can play a land from the top of library after The Lunar Whale attacks") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Plains" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!
        val opponent = driver.state.turnOrder.first { it != you }

        // Crew partner: Grizzly Bears (power 2 satisfies Crew 1).
        val bears = driver.putCreatureOnBattlefield(you, "Grizzly Bears")
        val whale = driver.putPermanentOnBattlefield(you, "The Lunar Whale")
        driver.removeSummoningSickness(whale)

        // Crew the Whale (it becomes a creature) and declare it as an attacker.
        driver.submitSuccess(CrewVehicle(you, whale, listOf(bears)))
        driver.bothPass() // resolve the crew activation
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.submitSuccess(DeclareAttackers(you, mapOf(whale to opponent)))

        // Move to the postcombat main phase — the Whale "attacked this turn", so the gate is open.
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        val forestOnTop = driver.putCardOnTopOfLibrary(you, "Forest")
        driver.playLand(you, forestOnTop).isSuccess shouldBe true
        driver.findPermanent(you, "Forest") shouldNotBe null
    }

    test("can cast a spell from the top of library after The Lunar Whale attacks") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Plains" to 20), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val you = driver.activePlayer!!
        val opponent = driver.state.turnOrder.first { it != you }

        val bears = driver.putCreatureOnBattlefield(you, "Grizzly Bears")
        val whale = driver.putPermanentOnBattlefield(you, "The Lunar Whale")
        driver.removeSummoningSickness(whale)

        driver.submitSuccess(CrewVehicle(you, whale, listOf(bears)))
        driver.bothPass()
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.submitSuccess(DeclareAttackers(you, mapOf(whale to opponent)))
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        // Frogmite (an artifact creature) on top — any spell type may be cast from the top.
        val frogmiteOnTop = driver.putCardOnTopOfLibrary(you, "Frogmite")
        driver.giveMana(you, Color.BLUE, 4)

        driver.castSpell(you, frogmiteOnTop).isSuccess shouldBe true
        driver.bothPass()
        driver.state.getBattlefield(you).contains(frogmiteOnTop) shouldBe true
    }
})
