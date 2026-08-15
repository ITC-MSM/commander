package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.fin.cards.SidequestCatchAFish
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Sidequest: Catch a Fish // Cooking Campsite (FIN).
 *
 * Front upkeep ability: look at the top card of your library; if it's an artifact or creature
 * card you may put it into your hand, and only if you did do you create a Food token and
 * transform the enchantment into the Cooking Campsite land.
 *
 * Test 1 (happy path): top card is a creature — the controller takes it, gets a Food token, and
 * the enchantment transforms into Cooking Campsite (asserted on the same entity).
 *
 * Test 2 (reflexive gate): top card is a land — the "put into hand" reveal is declined (nothing
 * eligible), so there is no Food and no transform; the permanent is still the enchantment.
 */
class SidequestCatchAFishScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(SidequestCatchAFish))
        d.registerCard(PredefinedTokens.Food) // GameTestDriver doesn't auto-register tokens
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        return d
    }

    // Player 1 may not be active at game start — advance until it's player 1's upkeep.
    fun GameTestDriver.advanceToPlayer1Upkeep() {
        passPriorityUntil(Step.UPKEEP)
        var safety = 0
        while (activePlayer != player1 && safety < 50) {
            bothPass()
            passPriorityUntil(Step.UPKEEP)
            safety++
        }
    }

    // Resolve the beginning-of-upkeep trigger; it pauses on the look-at-top choice.
    fun GameTestDriver.resolveUpkeepTrigger() {
        var guard = 0
        while (!isPaused && guard++ < 40) {
            bothPass()
            if (state.step != Step.UPKEEP && state.stack.isEmpty()) break
        }
    }

    fun GameTestDriver.foodToken(playerId: EntityId): EntityId? = findPermanent(playerId, "Food")

    test("takes an artifact/creature top card, creates Food, and transforms") {
        val d = driver()
        val enchant = d.putPermanentOnBattlefield(d.player1, "Sidequest: Catch a Fish")
        val topCreature = d.putCardOnTopOfLibrary(d.player1, "Grizzly Bears")

        d.advanceToPlayer1Upkeep()
        d.resolveUpkeepTrigger()

        // The look-at-top offers the creature card; take it into hand.
        val pick = d.pendingDecision as SelectCardsDecision
        pick.options.contains(topCreature) shouldBe true
        d.submitDecision(d.player1, CardsSelectedResponse(decisionId = pick.id, selectedCards = listOf(topCreature)))
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // A Food token was created and the enchantment transformed into Cooking Campsite (same entity).
        d.foodToken(d.player1).shouldNotBeNull()
        d.getCardName(enchant) shouldBe "Cooking Campsite"
    }

    test("declines to transform when the top card is not an artifact or creature") {
        val d = driver()
        val enchant = d.putPermanentOnBattlefield(d.player1, "Sidequest: Catch a Fish")
        val topLand = d.putCardOnTopOfLibrary(d.player1, "Forest")

        d.advanceToPlayer1Upkeep()
        d.resolveUpkeepTrigger()

        // The land is not an artifact or creature, so it's not an eligible option. If the engine
        // still surfaces a (declinable) look-at-top prompt, decline it by selecting nothing.
        (d.pendingDecision as? SelectCardsDecision)?.let { pick ->
            pick.options.contains(topLand) shouldBe false
            d.submitDecision(d.player1, CardsSelectedResponse(decisionId = pick.id, selectedCards = emptyList()))
            while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()
        }

        // No card was put into hand this way → no Food and no transform.
        d.foodToken(d.player1).shouldBeNull()
        d.getCardName(enchant) shouldBe "Sidequest: Catch a Fish"
    }
})
