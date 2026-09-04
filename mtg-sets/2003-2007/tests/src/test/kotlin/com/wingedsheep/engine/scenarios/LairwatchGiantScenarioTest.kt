package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.LairwatchGiant
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Lairwatch Giant (LRW #29) — "This creature can block an additional creature each combat.
 * Whenever this creature blocks two or more creatures, it gains first strike until end of turn."
 *
 * The card exists to exercise `BlockEvent.minBlockedAttackers`, and the axis has exactly one way to
 * be wrong that still reads right on the card: a bar that is never enforced. `BlockEvent` fires for
 * *any* block, so an unread count gives a Giant that blocks one creature first strike too — which
 * no "it works" test would notice. Hence the pair: two blocks grants it, one block does not.
 *
 * The count is also why the trigger fires **once**. "Blocks two or more creatures" is a single
 * event, and the 2007-10-01 ruling is explicit that a Giant already blocking two doesn't trigger
 * again when pushed to three. Asserting the stack holds exactly one trigger is the cheap proof.
 *
 * The two-creature block itself is the first ability doing its job: without
 * `CanBlockAdditionalForCreatureGroup` the declaration would be rejected outright, so a successful
 * `declareBlockers` over two attackers covers it.
 */
class LairwatchGiantScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + LairwatchGiant)
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** [count] attackers for the active player, ready to attack this turn. */
    fun GameTestDriver.attackers(count: Int): List<EntityId> =
        (1..count).map {
            putCreatureOnBattlefield(player1, "Grizzly Bears").also { id -> removeSummoningSickness(id) }
        }

    test("blocking two creatures triggers once and grants first strike") {
        val d = driver()
        val p2 = d.getOpponent(d.player1)
        val bears = d.attackers(2)
        val giant = d.putCreatureOnBattlefield(p2, "Lairwatch Giant")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(d.player1, bears, p2).isSuccess shouldBe true
        d.bothPass()

        withClue("the extra-block static must let one Giant block both attackers") {
            d.declareBlockers(p2, mapOf(giant to bears)).isSuccess shouldBe true
        }

        withClue("'blocks two or more creatures' is one event, not one per blocked attacker") {
            d.stackSize shouldBe 1
        }
        withClue("first strike arrives only on resolution, not on declaration") {
            d.state.projectedState.hasKeyword(giant, Keyword.FIRST_STRIKE) shouldBe false
        }

        d.bothPass()
        d.state.projectedState.hasKeyword(giant, Keyword.FIRST_STRIKE) shouldBe true
    }

    test("blocking a single creature does not trigger") {
        val d = driver()
        val p2 = d.getOpponent(d.player1)
        val bears = d.attackers(2)
        val giant = d.putCreatureOnBattlefield(p2, "Lairwatch Giant")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(d.player1, bears, p2).isSuccess shouldBe true
        d.bothPass()

        // Blocking only one of the two attackers — the Giant *could* block both, and that is the
        // point: the bar is on the blocks actually declared, not on what it was allowed to do.
        d.declareBlockers(p2, mapOf(giant to listOf(bears[0]))).isSuccess shouldBe true

        withClue("one block is below the bar, so nothing goes on the stack") {
            d.stackSize shouldBe 0
        }
        d.state.projectedState.hasKeyword(giant, Keyword.FIRST_STRIKE) shouldBe false
    }
})
