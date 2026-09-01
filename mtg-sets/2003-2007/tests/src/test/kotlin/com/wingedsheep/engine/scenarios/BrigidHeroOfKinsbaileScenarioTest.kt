package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.BrigidHeroOfKinsbaile
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Brigid, Hero of Kinsbaile (LRW #6) — "{T}: Brigid deals 2 damage to each attacking or blocking
 * creature target player controls."
 *
 * The group carries three predicates and the card is wrong if any one of them is dropped:
 * `IsCreature`, the attacking-or-blocking state pair, and "the *target player* controls it" —
 * which is a bound player reference, not Brigid's controller. A filter that fell back to the
 * implicit "you control" reading would shoot Brigid's own team and read identically on the card.
 *
 * The negative half is the point: a creature the target player controls that is *not* in combat
 * must be untouched, and a creature in combat that someone else controls must be untouched too.
 */
class BrigidHeroOfKinsbaileScenarioTest : FunSpec({

    val brigidAbility = BrigidHeroOfKinsbaile.activatedAbilities.single().id

    fun GameTestDriver.graveyard(playerId: EntityId): List<EntityId> =
        state.getZone(ZoneKey(playerId, Zone.GRAVEYARD))

    test("only the target player's attacking creatures are hit") {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + BrigidHeroOfKinsbaile)
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val opponent = d.getOpponent(d.player1)
        val brigid = d.putCreatureOnBattlefield(d.player1, "Brigid, Hero of Kinsbaile")
        d.removeSummoningSickness(brigid)

        // The opponent brings one 2/2 to the fight and leaves another at home.
        val attackingBears = d.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        val homeBears = d.putCreatureOnBattlefield(opponent, "Grizzly Bears")
        d.removeSummoningSickness(attackingBears)
        // Our own 2/2, also attacking, must survive: it is not controlled by the target player.
        val ourBears = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        d.removeSummoningSickness(ourBears)

        // Hand the turn over so the opponent is the attacker.
        d.passPriorityUntil(Step.END)
        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(opponent, listOf(attackingBears), d.player1)
        d.passPriority(opponent)

        d.submit(
            ActivateAbility(
                d.player1,
                brigid,
                brigidAbility,
                targets = listOf(ChosenTarget.Player(opponent))
            )
        ).isSuccess shouldBe true
        d.bothPass()

        withClue("the attacking 2/2 took 2 and died") {
            d.graveyard(opponent).contains(attackingBears) shouldBe true
        }
        withClue("the 2/2 that stayed home was never attacking or blocking") {
            d.graveyard(opponent).contains(homeBears) shouldBe false
        }
        withClue("our own 2/2 is not controlled by the target player") {
            d.graveyard(d.player1).contains(ourBears) shouldBe false
        }
    }

    test("blockers count too, and Brigid taps to pay") {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + BrigidHeroOfKinsbaile)
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val opponent = d.getOpponent(d.player1)
        val brigid = d.putCreatureOnBattlefield(d.player1, "Brigid, Hero of Kinsbaile")
        d.removeSummoningSickness(brigid)
        val ours = d.putCreatureOnBattlefield(d.player1, "Hill Giant")
        d.removeSummoningSickness(ours)
        val blocker = d.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(d.player1, listOf(ours), opponent)
        d.passPriorityUntil(Step.DECLARE_BLOCKERS)
        d.declareBlockers(opponent, mapOf(blocker to listOf(ours)))
        // Blockers-declared leaves priority with the defending player; hand it back to us.
        if (d.priorityPlayer != d.player1) d.passPriority(d.priorityPlayer!!)

        d.submit(
            ActivateAbility(
                d.player1,
                brigid,
                brigidAbility,
                targets = listOf(ChosenTarget.Player(opponent))
            )
        ).isSuccess shouldBe true
        d.bothPass()

        withClue("the blocking 2/2 took 2 and died before combat damage") {
            d.graveyard(opponent).contains(blocker) shouldBe true
        }

        withClue("the ability is once per untap — Brigid is now tapped and can't pay again") {
            d.submitExpectFailure(
                ActivateAbility(
                    d.player1,
                    brigid,
                    brigidAbility,
                    targets = listOf(ChosenTarget.Player(opponent))
                )
            ).isSuccess shouldBe false
        }
    }
})
