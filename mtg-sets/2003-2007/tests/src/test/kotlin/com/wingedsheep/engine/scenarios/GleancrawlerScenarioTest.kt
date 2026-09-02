package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.Gleancrawler
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Gleancrawler — {3}{B/G}{B/G}{B/G} 6/6 Trample (Ravnica: City of Guilds #247)
 *
 * "At the beginning of your end step, return to your hand all creature cards in your graveyard
 *  that were put there from the battlefield this turn."
 *
 * The clause is `putIntoGraveyardFromBattlefieldThisTurn()` over your graveyard, so the test
 * pairs a creature that actually died this turn with one that was simply put into the graveyard
 * and checks only the first comes back. The 2005 ruling — creatures that died *before*
 * Gleancrawler arrived still return — is the second case.
 */
class GleancrawlerScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + Gleancrawler)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    fun GameTestDriver.bolt(target: com.wingedsheep.sdk.model.EntityId) {
        val bolt = putCardInHand(player1, "Lightning Bolt")
        giveMana(player1, Color.RED, 1)
        castSpell(player1, bolt, targets = listOf(target)).error shouldBe null
        bothPass()
    }

    test("a creature that died this turn returns at your end step; a card merely in the graveyard stays") {
        val d = driver()
        d.putCreatureOnBattlefield(d.player1, "Gleancrawler")
        val courser = d.putCreatureOnBattlefield(d.player1, "Centaur Courser")
        val neverOnBattlefield = d.putCardInGraveyard(d.player1, "Savannah Lions")

        d.bolt(courser)
        withClue("the Courser died") {
            (courser in d.getGraveyard(d.player1)) shouldBe true
        }

        d.passPriorityUntil(Step.END)
        d.bothPass()

        withClue("the creature that was put there from the battlefield this turn is back in hand") {
            (courser in d.getHand(d.player1)) shouldBe true
        }
        withClue("a creature card that never left the graveyard from the battlefield stays put") {
            (neverOnBattlefield in d.getGraveyard(d.player1)) shouldBe true
            (neverOnBattlefield in d.getHand(d.player1)) shouldBe false
        }
    }

    test("a creature that died before Gleancrawler arrived still returns (2005 ruling)") {
        val d = driver()
        val courser = d.putCreatureOnBattlefield(d.player1, "Centaur Courser")
        d.bolt(courser)
        d.putCreatureOnBattlefield(d.player1, "Gleancrawler")

        d.passPriorityUntil(Step.END)
        d.bothPass()

        (courser in d.getHand(d.player1)) shouldBe true
    }
})
