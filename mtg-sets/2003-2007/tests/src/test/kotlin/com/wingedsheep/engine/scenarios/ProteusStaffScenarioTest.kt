package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mrd.cards.ProteusStaff
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Proteus Staff (MRD #230) — "{2}{U}, {T}: Put target creature on the bottom of its owner's
 * library. That creature's controller reveals cards from the top of their library until they
 * reveal a creature card. The player puts that card onto the battlefield and the rest on the
 * bottom of their library in any order."
 *
 * Everything after the bottoming is scoped to the *target's* controller, not the Staff's, and the
 * target has already left the battlefield by the time that scope is resolved — so these tests
 * exercise the reveal against an opponent's library too, not just the easy same-player case.
 */
class ProteusStaffScenarioTest : FunSpec({

    val staffAbility = ProteusStaff.activatedAbilities.single().id

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + ProteusStaff)
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** {2}{U} in the pool, ready to pay for one activation. */
    fun GameTestDriver.fundActivation(player: EntityId) {
        giveMana(player, Color.BLUE, 1)
        giveColorlessMana(player, 2)
    }

    fun GameTestDriver.library(player: EntityId): List<EntityId> =
        state.getZone(ZoneKey(player, Zone.LIBRARY))

    fun GameTestDriver.setLibrary(player: EntityId, cards: List<EntityId>) {
        replaceState(state.copy(zones = state.zones + (ZoneKey(player, Zone.LIBRARY) to cards)))
    }

    test("bottoms the target and puts the first creature revealed onto the battlefield") {
        val d = driver()
        val staff = d.putPermanentOnBattlefield(d.player1, "Proteus Staff")
        val victim = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        d.fundActivation(d.player1)

        // Top of library: one Swamp, then a Centaur Courser to be revealed under it.
        val courser = d.putCardOnTopOfLibrary(d.player1, "Centaur Courser")
        val swamp = d.putCardOnTopOfLibrary(d.player1, "Swamp")

        d.submitSuccess(
            ActivateAbility(
                d.player1,
                staff,
                staffAbility,
                targets = listOf(ChosenTarget.Permanent(victim))
            )
        )
        d.bothPass()

        withClue("the revealed creature replaces the bottomed one") {
            d.getCreatures(d.player1) shouldBe listOf(courser)
        }
        val library = d.library(d.player1)
        withClue("the target is bottomed first, then the reveals land under it") {
            library.takeLast(2) shouldBe listOf(victim, swamp)
        }
        withClue("the creature that entered is no longer in the library") {
            library.contains(courser) shouldBe false
        }
    }

    test("the target's controller reveals from their own library and orders their own leftovers") {
        val d = driver()
        val staff = d.putPermanentOnBattlefield(d.player1, "Proteus Staff")
        val victim = d.putCreatureOnBattlefield(d.player2, "Grizzly Bears")
        d.fundActivation(d.player1)

        val courser = d.putCardOnTopOfLibrary(d.player2, "Centaur Courser")
        val second = d.putCardOnTopOfLibrary(d.player2, "Swamp")
        val first = d.putCardOnTopOfLibrary(d.player2, "Swamp")

        d.submitSuccess(
            ActivateAbility(
                d.player1,
                staff,
                staffAbility,
                targets = listOf(ChosenTarget.Permanent(victim))
            )
        )
        d.bothPass()

        withClue("two non-creature cards were revealed, so their owner orders them") {
            val decision = d.state.pendingDecision
            (decision as? ReorderLibraryDecision)?.playerId shouldBe d.player2
            d.submitOrderedResponse(d.player2, listOf(second, first)).isSuccess shouldBe true
        }

        withClue("the revealed creature enters under the target's controller, not the Staff's") {
            d.getCreatures(d.player2) shouldBe listOf(courser)
            d.getCreatures(d.player1) shouldBe emptyList()
        }
        d.library(d.player2).takeLast(3) shouldBe listOf(victim, second, first)
    }

    test("with no other creature in the library the target is revealed and comes straight back") {
        val d = driver()
        val staff = d.putPermanentOnBattlefield(d.player1, "Proteus Staff")
        val victim = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        d.fundActivation(d.player1)

        // A two-card, creature-free library. The target is bottomed *first*, so it is the only
        // creature card the reveal can find — and the reveal walks the whole library to find it.
        val top = d.putCardOnTopOfLibrary(d.player1, "Swamp")
        val next = d.putCardOnTopOfLibrary(d.player1, "Swamp")
        d.setLibrary(d.player1, listOf(top, next))

        d.submitSuccess(
            ActivateAbility(
                d.player1,
                staff,
                staffAbility,
                targets = listOf(ChosenTarget.Permanent(victim))
            )
        )
        d.bothPass()
        (d.state.pendingDecision as? ReorderLibraryDecision)?.playerId shouldBe d.player1
        d.submitOrderedResponse(d.player1, listOf(top, next)).isSuccess shouldBe true

        withClue("the bottomed target is the creature the reveal finds, so it returns") {
            d.getCreatures(d.player1).map { d.getCardName(it) } shouldBe listOf("Grizzly Bears")
        }
        withClue("only the two revealed non-creature cards are left in the library") {
            d.library(d.player1) shouldBe listOf(top, next)
        }
    }
})
