package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.MemoriesReturning
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Tests for Memories Returning.
 *
 * Memories Returning: {2}{U}{U}
 * Sorcery
 * Reveal the top five cards of your library. Put one of them into your hand. Then choose an
 * opponent. They put one on the bottom of your library. Then you put one into your hand. Then
 * they put one on the bottom of your library. Put the other into your hand.
 * Flashback {7}{U}{U}
 */
class MemoriesReturningScenarioTest : FunSpec({

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(MemoriesReturning))
        return driver
    }

    fun GameTestDriver.setLibrary(player: EntityId, cardNames: List<String>) {
        var s = state
        for (id in s.getLibrary(player)) {
            s = s.removeFromZone(ZoneKey(player, Zone.LIBRARY), id)
        }
        replaceState(s)
        for (name in cardNames.reversed()) {
            putCardOnTopOfLibrary(player, name)
        }
    }

    fun GameTestDriver.pickByName(decisionFor: EntityId, name: String) {
        val decision = pendingDecision
        decision.shouldBeInstanceOf<SelectCardsDecision>()
        decision.playerId shouldBe decisionFor
        val chosen = decision.options.first { getCardName(it) == name }
        submitDecision(decisionFor, CardsSelectedResponse(decision.id, listOf(chosen)))
    }

    test("partitions the revealed five: three you choose to hand, two an opponent buries on the bottom") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 20, "Forest" to 20), startingLife = 20)
        val activePlayer = driver.activePlayer!!
        val opponent = driver.getOpponent(activePlayer)

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.setLibrary(
            activePlayer,
            listOf("Grizzly Bears", "Centaur Courser", "Llanowar Elves", "Phantom Warrior", "Goblin Guide")
        )

        val card = driver.putCardInHand(activePlayer, "Memories Returning")
        driver.giveMana(activePlayer, Color.BLUE, 2)
        driver.giveColorlessMana(activePlayer, 2)
        driver.castSpell(activePlayer, card, emptyList()).isSuccess shouldBe true
        driver.bothPass()

        // 1) You put one into your hand.
        driver.pickByName(activePlayer, "Grizzly Bears")
        // 2) Opponent puts one on the bottom of your library.
        driver.pickByName(opponent, "Centaur Courser")
        // 3) You put one into your hand.
        driver.pickByName(activePlayer, "Llanowar Elves")
        // 4) Opponent puts one on the bottom of your library.
        driver.pickByName(opponent, "Phantom Warrior")
        // 5) The last card ("the other") goes to your hand automatically.

        val handNames = driver.getHand(activePlayer).map { driver.getCardName(it) }
        handNames.filter {
            it in listOf("Grizzly Bears", "Llanowar Elves", "Goblin Guide")
        } shouldContainExactlyInAnyOrder listOf("Grizzly Bears", "Llanowar Elves", "Goblin Guide")

        // The two opponent-chosen cards are on the bottom of the library (the only cards left there).
        val libraryNames = driver.state.getLibrary(activePlayer).map { driver.getCardName(it) }
        libraryNames shouldContainExactlyInAnyOrder listOf("Centaur Courser", "Phantom Warrior")
    }
})
