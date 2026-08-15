package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.RandomEncounter
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for Random Encounter.
 *
 * Random Encounter: {4}{R}{R}
 * Sorcery
 * Shuffle your library, then mill four cards. Put each creature card milled this way onto the
 * battlefield. They gain haste. At the beginning of the next end step, return those creatures
 * to their owner's hand.
 * Flashback {6}{R}{R}
 */
class RandomEncounterScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(RandomEncounter))
        return driver
    }

    /** Replace [player]'s library with exactly the given cards (top-first), so a shuffle-then-mill is deterministic. */
    fun GameTestDriver.setLibrary(player: EntityId, cardNames: List<String>) {
        var s = state
        for (id in s.getLibrary(player)) {
            s = s.removeFromZone(ZoneKey(player, Zone.LIBRARY), id)
        }
        replaceState(s)
        // putCardOnTopOfLibrary pushes onto the top; insert in reverse so the list ends up top-first.
        for (name in cardNames.reversed()) {
            putCardOnTopOfLibrary(player, name)
        }
    }

    fun castRandomEncounter(driver: GameTestDriver, player: EntityId): EntityId {
        val card = driver.putCardInHand(player, "Random Encounter")
        driver.giveMana(player, Color.RED, 2)
        driver.giveColorlessMana(player, 4)
        val result = driver.castSpell(player, card, emptyList())
        result.isSuccess shouldBe true
        driver.bothPass()
        return card
    }

    test("mills four, puts the creature cards onto the battlefield, leaves non-creatures in the graveyard") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Forest" to 20), startingLife = 20)
        val activePlayer = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Deterministic top four: two creatures + two lands.
        driver.setLibrary(activePlayer, listOf("Grizzly Bears", "Forest", "Grizzly Bears", "Forest"))

        castRandomEncounter(driver, activePlayer)

        // Both creatures entered the battlefield under the controller's control.
        val battlefieldBears = driver.getPermanents(activePlayer)
            .count { driver.getCardName(it) == "Grizzly Bears" }
        battlefieldBears shouldBe 2

        // The two milled lands stayed in the graveyard; no creature cards left behind there.
        val graveyard = driver.getGraveyardCardNames(activePlayer)
        graveyard.count { it == "Forest" } shouldBe 2
        graveyard.count { it == "Grizzly Bears" } shouldBe 0
    }

    test("reanimated creatures have haste (can attack the turn they enter)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Forest" to 20), startingLife = 20)
        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.setLibrary(activePlayer, listOf("Grizzly Bears", "Grizzly Bears", "Grizzly Bears", "Grizzly Bears"))

        castRandomEncounter(driver, activePlayer)

        val bears = driver.getPermanents(activePlayer).first { driver.getCardName(it) == "Grizzly Bears" }

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        val attackResult = driver.declareAttackers(activePlayer, listOf(bears), opponent)
        // Without haste this would be rejected (summoning sickness).
        attackResult.isSuccess shouldBe true
    }

    test("returns the reanimated creatures to their owner's hand at the next end step") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Forest" to 20), startingLife = 20)
        val activePlayer = driver.activePlayer!!

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.setLibrary(activePlayer, listOf("Grizzly Bears", "Grizzly Bears", "Grizzly Bears", "Grizzly Bears"))

        val handBefore = driver.getHandSize(activePlayer)
        castRandomEncounter(driver, activePlayer)

        driver.getPermanents(activePlayer).count { driver.getCardName(it) == "Grizzly Bears" } shouldBe 4

        // Advance to the end step; the four per-creature delayed triggers fire and resolve.
        driver.passPriorityUntil(Step.END)
        repeat(8) {
            if (driver.getPermanents(activePlayer).any { driver.getCardName(it) == "Grizzly Bears" }) {
                driver.bothPass()
            }
        }

        driver.getPermanents(activePlayer).count { driver.getCardName(it) == "Grizzly Bears" } shouldBe 0
        // Four creatures are now in hand (Random Encounter left the hand when cast).
        driver.getHand(activePlayer).count { driver.getCardName(it) == "Grizzly Bears" } shouldBe 4
        driver.getHandSize(activePlayer) shouldNotBe handBefore
    }
})
