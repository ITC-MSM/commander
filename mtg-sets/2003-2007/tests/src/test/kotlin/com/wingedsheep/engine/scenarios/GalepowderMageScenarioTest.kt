package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.GalepowderMage
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Galepowder Mage (LRW #15) — "Whenever this creature attacks, exile another target creature.
 * Return that card to the battlefield under its owner's control at the beginning of the next
 * end step."
 *
 * A blink built from an exile plus a delayed end-step trigger has one way to fail that a
 * board-state assertion at end of turn would miss entirely: the delayed trigger holds a
 * reference to a card that has *changed zones*, and a reference that doesn't survive the move
 * leaves the creature exiled forever. So the exile and the return are asserted separately, with
 * a checkpoint in between.
 *
 * The second test pins the "under its **owner's** control" clause. "Another target creature" is
 * not restricted to creatures you control, so the natural aggressive line is to blink an
 * opposing blocker — and that creature must come back to its owner, not to whoever exiled it.
 */
class GalepowderMageScenarioTest : FunSpec({

    fun GameTestDriver.battlefield(playerId: EntityId): List<EntityId> =
        state.getZone(ZoneKey(playerId, Zone.BATTLEFIELD))

    fun GameTestDriver.exile(): List<EntityId> =
        state.zones.filterKeys { it.zoneType == Zone.EXILE }.values.flatten()

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + GalepowderMage)
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("the target leaves on attack and comes back at the beginning of the end step") {
        val d = driver()
        val mage = d.putCreatureOnBattlefield(d.player1, "Galepowder Mage")
        d.removeSummoningSickness(mage)
        val ours = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(d.player1, listOf(mage), d.getOpponent(d.player1))
        d.submitTargetSelection(d.player1, listOf(ours))
        d.bothPass()

        withClue("the attack trigger exiled the target immediately") {
            d.exile().contains(ours) shouldBe true
            d.battlefield(d.player1).contains(ours) shouldBe false
        }

        d.passPriorityUntil(Step.END)
        d.bothPass()

        withClue("the delayed trigger returned the card — a reference that died on the zone change would strand it") {
            d.exile().any { it == ours } shouldBe false
        }
        withClue("a Grizzly Bears is back on our battlefield") {
            d.battlefield(d.player1).any {
                d.getCardName(it) == "Grizzly Bears"
            } shouldBe true
        }
    }

    test("an opposing creature returns under its owner's control, not ours") {
        val d = driver()
        val opponent = d.getOpponent(d.player1)
        val mage = d.putCreatureOnBattlefield(d.player1, "Galepowder Mage")
        d.removeSummoningSickness(mage)
        val theirs = d.putCreatureOnBattlefield(opponent, "Grizzly Bears")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(d.player1, listOf(mage), opponent)
        d.submitTargetSelection(d.player1, listOf(theirs))
        d.bothPass()

        d.passPriorityUntil(Step.END)
        d.bothPass()

        withClue("the opponent's creature came back to the opponent") {
            d.battlefield(opponent).any {
                d.getCardName(it) == "Grizzly Bears"
            } shouldBe true
        }
        withClue("and not to us") {
            d.battlefield(d.player1).any {
                d.getCardName(it) == "Grizzly Bears"
            } shouldBe false
        }
    }

    // "The exiled card will be returned to the battlefield at the beginning of the end step even
    // if Galepowder Mage is no longer on the battlefield at that time." (2007-10-01)
    //
    // The delayed trigger is a free-standing object, not a linked ability gated on its source, so
    // killing the Mage must not strand the card in exile — the failure mode a source-gated
    // implementation would produce.
    test("the card comes back even after the Mage has left the battlefield") {
        val d = driver()
        val mage = d.putCreatureOnBattlefield(d.player1, "Galepowder Mage")
        d.removeSummoningSickness(mage)
        val ours = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(d.player1, listOf(mage), d.getOpponent(d.player1))
        d.submitTargetSelection(d.player1, listOf(ours))
        d.bothPass()
        d.exile().contains(ours) shouldBe true

        d.moveToGraveyard(mage)

        d.passPriorityUntil(Step.END)
        d.bothPass()

        withClue("the delayed trigger fired without its source") {
            d.exile().contains(ours) shouldBe false
            d.battlefield(d.player1).any {
                d.getCardName(it) == "Grizzly Bears"
            } shouldBe true
        }
    }
})
