package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.DolmenGate
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Dolmen Gate (LRW #256) — "Prevent all combat damage that would be dealt to attacking creatures
 * you control."
 *
 * The shield's recipient is a *live* filter, not a fixed entity, and every axis of it can fail
 * open or fail closed without changing how the card reads:
 *
 *  - **attacking** — a shield that ignores the state predicate protects your blockers and your
 *    creatures sitting at home too, which is a strictly better card.
 *  - **you control** — a shield that ignores the controller predicate protects the opponent's
 *    attackers as well, which is a strictly worse one.
 *  - **direction** — a shield that also covered damage dealt *by* the attacker would be Fog Bank,
 *    not Dolmen Gate.
 *
 * Nothing in the corpus had previously combined `RecipientFilter.Matching` with a *state*
 * predicate, so each axis gets its own assertion rather than one "the attacker lived".
 */
class DolmenGateScenarioTest : FunSpec({

    fun GameTestDriver.graveyard(playerId: EntityId): List<EntityId> =
        state.getZone(ZoneKey(playerId, Zone.GRAVEYARD))

    /**
     * Player 1 on the attack with the Gate out, stopped in the declare-blockers step with
     * player 2 still to declare.
     */
    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + DolmenGate)
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("your attacker survives a bigger blocker; the blocker still takes its damage") {
        val d = driver()
        val opponent = d.getOpponent(d.player1)
        d.putPermanentOnBattlefield(d.player1, "Dolmen Gate")

        // A 2/2 of ours into a 3/3 of theirs: without the Gate our attacker dies, with it the
        // 3/3 still dies to our 2 damage because the shield is one-directional.
        val ours = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        d.removeSummoningSickness(ours)
        val theirs = d.putCreatureOnBattlefield(opponent, "Hill Giant")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(d.player1, listOf(ours), opponent)
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareBlockers(opponent, mapOf(theirs to listOf(ours)))
        d.passPriorityUntil(Step.END)

        withClue("the Gate prevented the 3 combat damage dealt to our attacker") {
            d.graveyard(d.player1).contains(ours) shouldBe false
        }
        withClue("our attacker's own 2 damage was not prevented, but a 3/3 survives it") {
            d.graveyard(opponent).contains(theirs) shouldBe false
        }
    }

    test("the shield does not cover a creature you control that is blocking") {
        val d = driver()
        val opponent = d.getOpponent(d.player1)
        d.putPermanentOnBattlefield(d.player1, "Dolmen Gate")

        // Hand the turn to the opponent so they are the attacker and we are the blocker.
        val theirs = d.putCreatureOnBattlefield(opponent, "Hill Giant")
        val ours = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        d.removeSummoningSickness(theirs)

        d.passPriorityUntil(Step.END)
        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(opponent, listOf(theirs), d.player1)
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareBlockers(d.player1, mapOf(ours to listOf(theirs)))
        d.passPriorityUntil(Step.END)

        withClue("our 2/2 was blocking, not attacking, so the Gate did nothing for it") {
            d.graveyard(d.player1).contains(ours) shouldBe true
        }
    }

    test("the shield does not cover an opponent's attacking creature") {
        val d = driver()
        val opponent = d.getOpponent(d.player1)
        d.putPermanentOnBattlefield(d.player1, "Dolmen Gate")

        val theirs = d.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        val ours = d.putCreatureOnBattlefield(d.player1, "Hill Giant")
        d.removeSummoningSickness(theirs)

        d.passPriorityUntil(Step.END)
        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(opponent, listOf(theirs), d.player1)
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareBlockers(d.player1, mapOf(ours to listOf(theirs)))
        d.passPriorityUntil(Step.END)

        withClue("their 2/2 was attacking, but we control the Gate — it takes our 3 and dies") {
            d.graveyard(opponent).contains(theirs) shouldBe true
        }
    }
})
