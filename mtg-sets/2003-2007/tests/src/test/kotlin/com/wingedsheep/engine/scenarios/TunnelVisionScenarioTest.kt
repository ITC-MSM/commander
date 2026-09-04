package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.rav.cards.TunnelVision
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tunnel Vision (RAV #72) — "Choose a card name. Target player reveals cards from the top of their
 * library until a card with that name is revealed. If it is, that player puts the rest of the
 * revealed cards into their graveyard and puts the card with the chosen name on top of their
 * library. Otherwise, the player shuffles."
 *
 * The two branches are the card, and the 2005-10-01 ruling states both: a **hit** bins everything
 * above the named card and leaves that card on top without shuffling; a **miss** bins nothing and
 * shuffles. The miss is the dangerous branch — the walk has revealed the entire library by then, so
 * an unguarded "put the rest into the graveyard" would bin all of it.
 *
 * "Puts the card with the chosen name on top" needs no step of its own: the revealed pile is exactly
 * the run from the top down to the match, so removing everything above it leaves it on top.
 */
class TunnelVisionScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + TunnelVision)
        d.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Casts Tunnel Vision at [victim], naming [name], and resolves it out. */
    fun GameTestDriver.tunnelVision(caster: EntityId, victim: EntityId, name: String) {
        giveMana(caster, Color.BLUE, 6)
        val spell = putCardInHand(caster, "Tunnel Vision")
        castSpellWithTargets(caster, spell, listOf(ChosenTarget.Player(victim))).isSuccess shouldBe true
        var guard = 0
        while (stackSize > 0 && pendingDecision == null && guard++ < 10) bothPass()

        val decision = pendingDecision as? ChooseOptionDecision
            ?: error("Expected a card-name choice from Tunnel Vision, got $pendingDecision")
        val index = decision.options.indexOf(name)
        check(index >= 0) { "'$name' is not among the offered card names" }
        submitDecision(caster, OptionChosenResponse(decision.id, index))

        guard = 0
        while ((stackSize > 0 || pendingDecision != null) && guard++ < 20) {
            if (pendingDecision != null) autoResolveDecision() else bothPass()
        }
    }

    fun GameTestDriver.library(playerId: EntityId): List<EntityId> =
        state.getZone(ZoneKey(playerId, Zone.LIBRARY))

    test("a hit bins every card above the named one and leaves it on top") {
        val d = driver()
        val me = d.player1
        val opp = d.player2

        // Built bottom-up: putCardOnTopOfLibrary prepends, so the last call ends up on top.
        d.putCardOnTopOfLibrary(opp, "Phantom Warrior")
        d.putCardOnTopOfLibrary(opp, "Savannah Lions")
        d.putCardOnTopOfLibrary(opp, "Centaur Courser")
        val librarySizeBefore = d.library(opp).size

        d.tunnelVision(me, opp, "Phantom Warrior")

        withClue("only the cards revealed above the named one are binned") {
            d.getGraveyardCardNames(opp) shouldBe listOf("Centaur Courser", "Savannah Lions")
        }
        withClue("the named card is left on top of the library") {
            d.getCardName(d.library(opp).first()) shouldBe "Phantom Warrior"
        }
        d.library(opp).size shouldBe librarySizeBefore - 2
    }

    test("naming the card already on top bins nothing and leaves the library alone") {
        val d = driver()
        val me = d.player1
        val opp = d.player2

        d.putCardOnTopOfLibrary(opp, "Savannah Lions")
        d.putCardOnTopOfLibrary(opp, "Phantom Warrior")
        val librarySizeBefore = d.library(opp).size

        d.tunnelVision(me, opp, "Phantom Warrior")

        d.getGraveyardCardNames(opp) shouldBe emptyList()
        d.getCardName(d.library(opp).first()) shouldBe "Phantom Warrior"
        d.library(opp).size shouldBe librarySizeBefore
    }

    test("a miss bins nothing — the whole library stays put and is shuffled instead") {
        val d = driver()
        val me = d.player1
        val opp = d.player2

        d.putCardOnTopOfLibrary(opp, "Savannah Lions")
        val librarySizeBefore = d.library(opp).size

        // Lightning Bolt is registered but nowhere in the opponent's all-Island library.
        d.tunnelVision(me, opp, "Lightning Bolt")

        withClue("the ruling is explicit: a name that isn't in the library bins nothing") {
            d.getGraveyardCardNames(opp) shouldBe emptyList()
        }
        withClue("the walk revealed the whole library, and every card of it stays there") {
            d.library(opp).size shouldBe librarySizeBefore
        }
    }
})
