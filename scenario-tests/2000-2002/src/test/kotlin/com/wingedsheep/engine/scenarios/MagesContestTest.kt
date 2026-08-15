package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseNumberDecision
import com.wingedsheep.engine.core.NumberChosenResponse
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.inv.cards.MagesContest
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Mages' Contest (INV #154) — Invasion engine gap #16: open life-bidding auction.
 *
 * "You and target spell's controller bid life. You start the bidding with a bid of 1.
 * In turn order, each player may top the high bid. The bidding ends if the high bid stands.
 * The high bidder loses life equal to the high bid. If you win the bidding, counter that spell."
 *
 * The active player ("opp") casts a Lightning Bolt; the other player ("you") responds with
 * Mages' Contest targeting it, opening the bid at 1.
 */
class MagesContestTest : FunSpec({

    /**
     * Sets up a Lightning Bolt on the stack (cast by the active player at "opp") and Mages'
     * Contest cast in response by "you", resolving until the auction's first decision pauses.
     * Returns the driver plus (you, opp, bolt).
     */
    fun setup(): Triple<GameTestDriver, Triple<com.wingedsheep.sdk.model.EntityId, com.wingedsheep.sdk.model.EntityId, com.wingedsheep.sdk.model.EntityId>, Unit> {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(MagesContest))
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val opp = driver.activePlayer!!            // casts the spell that gets bid over
        val you = driver.getOpponent(opp)          // casts Mages' Contest

        // Opp casts Lightning Bolt at you, then passes priority.
        val bolt = driver.putCardInHand(opp, "Lightning Bolt")
        driver.giveMana(opp, Color.RED, 1)
        driver.castSpell(opp, bolt, listOf(you)).isSuccess shouldBe true
        driver.state.stack.contains(bolt) shouldBe true
        driver.passPriority(opp)

        // You respond with Mages' Contest targeting the Bolt.
        val contest = driver.putCardInHand(you, "Mages' Contest")
        driver.giveMana(you, Color.RED, 3)
        driver.castSpellWithTargets(you, contest, listOf(ChosenTarget.Spell(bolt))).isSuccess shouldBe true

        // Resolve Mages' Contest — the auction opens, asking opp whether to top the bid of 1.
        driver.bothPass()

        return Triple(driver, Triple(you, opp, bolt), Unit)
    }

    test("opponent passes — you win at 1, lose 1 life, and counter the spell") {
        val (driver, ids, _) = setup()
        val (you, _, bolt) = ids

        driver.pendingDecision.shouldBeInstanceOf<YesNoDecision>()
        // Opponent declines to top the opening bid of 1.
        driver.submitYesNo((driver.pendingDecision as YesNoDecision).playerId, false)

        // You won the bidding: lose 1 life and the Bolt is countered.
        driver.getLifeTotal(you) shouldBe 19
        driver.state.stack.contains(bolt) shouldBe false
    }

    test("opponent outbids and you pass — opponent loses the bid and the spell resolves") {
        val (driver, ids, _) = setup()
        val (you, opp, bolt) = ids

        // Opponent tops the bid...
        val topDecision = driver.pendingDecision as YesNoDecision
        topDecision.playerId shouldBe opp
        driver.submitYesNo(opp, true)

        // ...by bidding 3 life.
        val amount = driver.pendingDecision as ChooseNumberDecision
        amount.minValue shouldBe 2 // must exceed the high bid of 1
        driver.submitDecision(opp, NumberChosenResponse(amount.id, 3))

        // Now you're asked to top the bid of 3 — you pass.
        val yourTurn = driver.pendingDecision as YesNoDecision
        yourTurn.playerId shouldBe you
        driver.submitYesNo(you, false)

        // Opponent won the bidding: loses 3 life, and the Bolt is NOT countered.
        driver.getLifeTotal(opp) shouldBe 17
        driver.state.stack.contains(bolt) shouldBe true

        // The Bolt now resolves, dealing 3 to you.
        driver.bothPass()
        driver.state.stack.contains(bolt) shouldBe false
        driver.getLifeTotal(you) shouldBe 17
    }
})
