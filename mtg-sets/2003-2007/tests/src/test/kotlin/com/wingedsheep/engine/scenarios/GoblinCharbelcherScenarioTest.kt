package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mrd.cards.GoblinCharbelcher
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Goblin Charbelcher (MRD #176) — "{3}, {T}: Reveal cards from the top of your library until you
 * reveal a land card. This artifact deals damage equal to the number of nonland cards revealed this
 * way to any target. If the revealed land card was a Mountain, this artifact deals double that
 * damage instead. Put the revealed cards on the bottom of your library in any order."
 *
 * Three things can quietly go wrong here and only one of them is the Mountain clause. The damage
 * counts the *nonlands*, not the reveal — those differ by exactly one whenever a stopper was found,
 * so a card that fed the whole pile into the damage amount would look right on a Mountain-less
 * board and be off by one everywhere. The doubling has to replace the amount rather than add a
 * second damage event ("double that damage instead"). And running the library out is the no-stopper
 * path: the Mountain condition must read false off an *empty* match collection rather than throw,
 * which is the 2016-06-08 ruling.
 *
 * Each test drives a hand-built library so the reveal window is exact, and checks the bottoming as
 * well — the library must come back the same size with the revealed cards moved underneath.
 */
class GoblinCharbelcherScenarioTest : FunSpec({

    val belchAbility = GoblinCharbelcher.activatedAbilities.single().id

    /** Player 1's precombat main, with their library emptied so the test can stack it exactly. */
    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + GoblinCharbelcher)
        d.initMirrorMatch(deck = Deck.of("Mountain" to 30, "Forest" to 30), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val library = ZoneKey(d.player1, Zone.LIBRARY)
        d.replaceState(d.state.copy(zones = d.state.zones + (library to emptyList())))
        return d
    }

    fun GameTestDriver.library(): List<EntityId> = state.getZone(ZoneKey(player1, Zone.LIBRARY))

    /** Activate the belcher at the opponent's face and let it resolve. */
    fun GameTestDriver.belchAt(opponent: EntityId): EntityId {
        val belcher = putPermanentOnBattlefield(player1, "Goblin Charbelcher")
        giveColorlessMana(player1, 3)
        submit(
            ActivateAbility(player1, belcher, belchAbility, targets = listOf(ChosenTarget.Player(opponent)))
        ).isSuccess shouldBe true
        bothPass()
        return belcher
    }

    test("damage counts the nonlands revealed, not the whole reveal") {
        val d = driver()
        val opponent = d.getOpponent(d.player1)

        // Bottom-up, because putCardOnTopOfLibrary prepends:
        // top -> Lightning Bolt, Grizzly Bears, Island (the stopper), Centaur Courser.
        val deepCard = d.putCardOnTopOfLibrary(d.player1, "Centaur Courser")
        val stopper = d.putCardOnTopOfLibrary(d.player1, "Island")
        val nonland2 = d.putCardOnTopOfLibrary(d.player1, "Grizzly Bears")
        val nonland1 = d.putCardOnTopOfLibrary(d.player1, "Lightning Bolt")

        d.belchAt(opponent)

        withClue("two nonlands revealed above a non-Mountain land — three cards seen, two counted") {
            d.getLifeTotal(opponent) shouldBe 18
        }

        // Bottoming the three revealed cards asks for an order.
        d.pendingDecision.shouldBeInstanceOf<ReorderLibraryDecision>()
        val reorder = d.pendingDecision as ReorderLibraryDecision
        reorder.cards.toSet() shouldBe setOf(nonland1, nonland2, stopper)
        d.submitOrderedResponse(d.player1, listOf(nonland1, nonland2, stopper))
        d.isPaused shouldBe false

        withClue("the reveal is bottomed, not drawn or exiled — same four cards, new order") {
            d.library().size shouldBe 4
            d.library().first() shouldBe deepCard
            d.library().takeLast(3) shouldBe listOf(nonland1, nonland2, stopper)
        }
    }

    test("a Mountain stopper doubles the damage") {
        val d = driver()
        val opponent = d.getOpponent(d.player1)

        // Same shape as above, with the stopper swapped for a Mountain.
        d.putCardOnTopOfLibrary(d.player1, "Centaur Courser")
        val stopper = d.putCardOnTopOfLibrary(d.player1, "Mountain")
        val nonland2 = d.putCardOnTopOfLibrary(d.player1, "Grizzly Bears")
        val nonland1 = d.putCardOnTopOfLibrary(d.player1, "Lightning Bolt")

        d.belchAt(opponent)

        withClue("\"double that damage instead\" replaces the amount: 2 nonlands -> 4") {
            d.getLifeTotal(opponent) shouldBe 16
        }

        d.submitOrderedResponse(d.player1, listOf(nonland1, nonland2, stopper))
        d.isPaused shouldBe false
        d.library().size shouldBe 4
    }

    test("a landless library is revealed whole and every card counts") {
        val d = driver()
        val opponent = d.getOpponent(d.player1)

        val third = d.putCardOnTopOfLibrary(d.player1, "Centaur Courser")
        val second = d.putCardOnTopOfLibrary(d.player1, "Grizzly Bears")
        val first = d.putCardOnTopOfLibrary(d.player1, "Lightning Bolt")

        d.belchAt(opponent)

        withClue("no stopper: the Mountain condition reads false off an empty match, all 3 count") {
            d.getLifeTotal(opponent) shouldBe 17
        }

        val reorder = d.pendingDecision as ReorderLibraryDecision
        reorder.cards.toSet() shouldBe setOf(first, second, third)
        d.submitOrderedResponse(d.player1, listOf(third, second, first))
        d.isPaused shouldBe false

        withClue("the whole library was revealed, so bottoming it just reorders it") {
            d.library() shouldBe listOf(third, second, first)
        }
    }
})
