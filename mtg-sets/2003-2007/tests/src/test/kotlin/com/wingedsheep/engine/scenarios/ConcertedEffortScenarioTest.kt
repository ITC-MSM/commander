package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe

/**
 * Concerted Effort (RAV #8) — "At the beginning of each upkeep, creatures you control gain flying
 * until end of turn if a creature you control has flying. The same is true for fear, first strike,
 * double strike, landwalk, protection, trample, and vigilance."
 *
 * The ruling's own example drives the main test: a flyer with protection from one colour, a
 * landwalker with protection from another and a vigilant creature share *all* of it — both
 * protections included — and keep it after the supplier leaves.
 */
class ConcertedEffortScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingLife = 20)
        return d
    }

    test("each creature you control gains every listed ability and protection a creature you control has") {
        val d = driver()
        val p1 = d.activePlayer!!
        val p2 = d.getOpponent(p1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putPermanentOnBattlefield(p1, "Concerted Effort")
        val angel = d.putCreatureOnBattlefield(p1, "Serra Angel")          // flying, vigilance
        val whiteKnight = d.putCreatureOnBattlefield(p1, "White Knight")   // first strike, pro black
        val blackKnight = d.putCreatureOnBattlefield(p1, "Black Knight")   // first strike, pro white
        val flotilla = d.putCreatureOnBattlefield(p1, "Goblin Flotilla")   // islandwalk
        val bears = d.putCreatureOnBattlefield(p1, "Grizzly Bears")
        val theirBears = d.putCreatureOnBattlefield(p2, "Grizzly Bears")

        // The next upkeep is the opponent's — "each upkeep" still triggers for p1.
        d.passPriorityUntil(Step.UPKEEP)
        d.bothPass()

        val projected = d.state.projectedState
        for (creature in listOf(angel, whiteKnight, blackKnight, flotilla, bears)) {
            projected.hasKeyword(creature, Keyword.FLYING) shouldBe true
            projected.hasKeyword(creature, Keyword.VIGILANCE) shouldBe true
            projected.hasKeyword(creature, Keyword.FIRST_STRIKE) shouldBe true
            projected.hasKeyword(creature, Keyword.ISLANDWALK) shouldBe true
            projected.getKeywords(creature) shouldContain "PROTECTION_FROM_BLACK"
            projected.getKeywords(creature) shouldContain "PROTECTION_FROM_WHITE"
            // Nobody had these, so nobody gains them.
            projected.hasKeyword(creature, Keyword.TRAMPLE) shouldBe false
            projected.hasKeyword(creature, Keyword.FEAR) shouldBe false
            projected.hasKeyword(creature, Keyword.DOUBLE_STRIKE) shouldBe false
            projected.hasKeyword(creature, Keyword.SWAMPWALK) shouldBe false
        }
        // An opponent's creature is not "a creature you control".
        projected.hasKeyword(theirBears, Keyword.FLYING) shouldBe false
        projected.getKeywords(theirBears) shouldNotContain "PROTECTION_FROM_BLACK"
    }

    test("gained abilities stay until end of turn after the supplier and the enchantment leave") {
        val d = driver()
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val effort = d.putPermanentOnBattlefield(p1, "Concerted Effort")
        val whiteKnight = d.putCreatureOnBattlefield(p1, "White Knight")
        val bears = d.putCreatureOnBattlefield(p1, "Grizzly Bears")

        d.passPriorityUntil(Step.UPKEEP)
        d.bothPass()

        d.moveToGraveyard(whiteKnight)
        d.moveToGraveyard(effort)

        val projected = d.state.projectedState
        projected.hasKeyword(bears, Keyword.FIRST_STRIKE) shouldBe true
        projected.getKeywords(bears) shouldContain "PROTECTION_FROM_BLACK"
    }

    test("with nothing to share, nothing is granted") {
        val d = driver()
        val p1 = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putPermanentOnBattlefield(p1, "Concerted Effort")
        val bears = d.putCreatureOnBattlefield(p1, "Grizzly Bears")

        d.passPriorityUntil(Step.UPKEEP)
        d.bothPass()

        val keywords = d.state.projectedState.getKeywords(bears)
        keywords.none { it.startsWith("PROTECTION") } shouldBe true
        d.state.projectedState.hasKeyword(bears, Keyword.FLYING) shouldBe false
    }
})
