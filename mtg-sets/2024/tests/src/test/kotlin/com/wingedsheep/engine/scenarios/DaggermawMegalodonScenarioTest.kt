package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.TypecycleCard
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Daggermaw Megalodon (DSK #48) — {4}{U}{U} 5/7 Creature — Shark.
 *   Vigilance
 *   Islandcycling {2} ({2}, Discard this card: Search your library for an Island card,
 *   reveal it, put it into your hand, then shuffle.)
 *
 * Verifies the printed creature has vigilance and that the Islandcycling variant searches
 * specifically for Islands.
 */
class DaggermawMegalodonScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply { registerCards(TestCards.all) }

    test("the printed creature has vigilance") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val shark = d.putCreatureOnBattlefield(you, "Daggermaw Megalodon")
        StateProjector().project(d.state).hasKeyword(shark, Keyword.VIGILANCE) shouldBe true
    }

    test("islandcycling discards and searches for an Island") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val shark = d.putCardInHand(you, "Daggermaw Megalodon")
        val island = d.putCardOnTopOfLibrary(you, "Island")
        d.giveColorlessMana(you, 2)

        val result = d.submit(TypecycleCard(playerId = you, cardId = shark))
        (result.isSuccess || result.isPaused).shouldBeTrue()
        d.getGraveyardCardNames(you) shouldContain "Daggermaw Megalodon"

        val decision = d.pendingDecision
        decision.shouldBeInstanceOf<SelectCardsDecision>()
        decision.options shouldContain island

        d.submitDecision(you, CardsSelectedResponse(decision.id, listOf(island)))
        d.findCardInHand(you, "Island") shouldBe island
    }
})
