package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.MoonlightBargain
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Moonlight Bargain — {3}{B}{B} Instant (Ravnica: City of Guilds #95)
 *
 * "Look at the top five cards of your library. For each card, put that card into your graveyard
 *  unless you pay 2 life. Then put the rest into your hand."
 *
 * One pay-2-life gate per looked-at card, in library order. The test answers the five prompts
 * pay / decline / pay / decline / decline and checks each card landed where its answer sent it,
 * then repeats at 3 life to show the third prompt is skipped outright once 2 life can't be paid.
 */
class MoonlightBargainScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + MoonlightBargain)
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Stack five Bears on top of player 1's library; the returned list runs top-down. */
    fun GameTestDriver.stackFive() = (1..5).map { putCardOnTopOfLibrary(player1, "Grizzly Bears") }.reversed()

    fun GameTestDriver.castBargain() {
        val bargain = putCardInHand(player1, "Moonlight Bargain")
        giveMana(player1, Color.BLACK, 2)
        giveColorlessMana(player1, 3)
        castSpell(player1, bargain).error shouldBe null
        bothPass()
    }

    test("each card is kept for 2 life or binned, top of library first") {
        val d = driver()
        val top = d.stackFive()
        val librarySizeBefore = d.state.getLibrary(d.player1).size
        d.castBargain()

        listOf(true, false, true, false, false).forEach { pay ->
            withClue("a pay-2-life prompt is pending") { (d.state.pendingDecision != null) shouldBe true }
            d.submitYesNo(d.player1, pay).error shouldBe null
        }

        withClue("the two paid-for cards are in hand") {
            (top[0] in d.getHand(d.player1)) shouldBe true
            (top[2] in d.getHand(d.player1)) shouldBe true
        }
        withClue("the three declined cards are in the graveyard") {
            (top[1] in d.getGraveyard(d.player1)) shouldBe true
            (top[3] in d.getGraveyard(d.player1)) shouldBe true
            (top[4] in d.getGraveyard(d.player1)) shouldBe true
        }
        withClue("2 life per kept card") {
            d.getLifeTotal(d.player1) shouldBe 16
        }
        withClue("exactly five cards left the library") {
            d.state.getLibrary(d.player1).size shouldBe librarySizeBefore - 5
        }
        withClue("no prompt is left over") {
            d.state.pendingDecision shouldBe null
        }
    }

    test("once 2 life can't be paid the remaining cards go to the graveyard without a prompt") {
        val d = driver()
        val top = d.stackFive()
        d.setLifeTotal(d.player1, 3)
        d.castBargain()

        d.submitYesNo(d.player1, true).error shouldBe null
        withClue("at 1 life nothing more can be paid, so the loop finishes on its own") {
            d.state.pendingDecision shouldBe null
        }
        d.getLifeTotal(d.player1) shouldBe 1
        (top[0] in d.getHand(d.player1)) shouldBe true
        top.drop(1).forEach { (it in d.getGraveyard(d.player1)) shouldBe true }
    }
})
