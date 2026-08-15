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
 * Bedhead Beastie (DSK #125) — {4}{R}{R} 5/6 Creature — Beast.
 *   Menace
 *   Mountaincycling {2} ({2}, Discard this card: Search your library for a Mountain card,
 *   reveal it, put it into your hand, then shuffle.)
 *
 * Menace is a core keyword (covered by combat tests); this verifies the Mountaincycling
 * variant searches specifically for Mountains, and that the printed creature has menace.
 */
class BedheadBeastieScenarioTest : FunSpec({

    fun driver(): GameTestDriver = GameTestDriver().apply { registerCards(TestCards.all) }

    test("the printed creature has menace") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val beastie = d.putCreatureOnBattlefield(you, "Bedhead Beastie")
        StateProjector().project(d.state).hasKeyword(beastie, Keyword.MENACE) shouldBe true
    }

    test("mountaincycling discards and searches for a Mountain") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20)
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val beastie = d.putCardInHand(you, "Bedhead Beastie")
        val mountain = d.putCardOnTopOfLibrary(you, "Mountain")
        d.giveColorlessMana(you, 2)

        val result = d.submit(TypecycleCard(playerId = you, cardId = beastie))
        (result.isSuccess || result.isPaused).shouldBeTrue()
        d.getGraveyardCardNames(you) shouldContain "Bedhead Beastie"

        val decision = d.pendingDecision
        decision.shouldBeInstanceOf<SelectCardsDecision>()
        decision.options shouldContain mountain

        d.submitDecision(you, CardsSelectedResponse(decision.id, listOf(mountain)))
        d.findCardInHand(you, "Mountain") shouldBe mountain
    }
})
