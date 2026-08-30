package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.jud.cards.MentalNote
import com.wingedsheep.mtg.sets.definitions.vow.cards.GrolnokTheOmnivore
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Grolnok, the Omnivore (VOW #238).
 *
 * What is being proved:
 *  - the attack trigger fires off Grolnok itself (it is a Frog) and mills three;
 *  - the library-to-graveyard trigger fires **per card** and stamps a croak counter on each
 *    permanent card it exiles, while a nonpermanent card milled in the same batch stays in the
 *    graveyard untouched;
 *  - per the first ruling the trigger is not scoped to Grolnok's own mill — a self-mill from an
 *    unrelated spell feeds it just the same;
 *  - the play permission is a live filter over exile: a croak-countered land is playable from
 *    exile, a land exiled without one is not.
 */
class GrolnokTheOmnivoreScenarioTest : FunSpec({

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all)
        d.registerCard(GrolnokTheOmnivore)
        d.registerCard(MentalNote)
        return d
    }

    fun croakCounters(d: GameTestDriver, id: EntityId): Int =
        d.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.CROAK) ?: 0

    test("attacking with Grolnok mills three and croak-exiles the permanent cards") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val me = d.activePlayer!!
        val opponent = d.getOpponent(me)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val grolnok = d.putCreatureOnBattlefield(me, "Grolnok, the Omnivore")
        d.removeSummoningSickness(grolnok)

        // Stack the top three. Pushed bottom-up, so the top is Island, then Centaur Courser,
        // then Shock — an instant, which must stay in the graveyard.
        d.putCardOnTopOfLibrary(me, "Shock")
        val courser = d.putCardOnTopOfLibrary(me, "Centaur Courser")
        val island = d.putCardOnTopOfLibrary(me, "Island")

        d.passPriorityUntil(Step.DECLARE_ATTACKERS)
        d.declareAttackers(me, listOf(grolnok), opponent).isSuccess shouldBe true
        repeat(6) { d.bothPass() }

        withClue("the two permanent cards were exiled; the instant stayed in the graveyard") {
            d.getExileCardNames(me).shouldContainExactlyInAnyOrder("Island", "Centaur Courser")
            d.getGraveyardCardNames(me) shouldBe listOf("Shock")
        }
        withClue("each exiled permanent got exactly one croak counter") {
            croakCounters(d, island) shouldBe 1
            croakCounters(d, courser) shouldBe 1
        }
    }

    test("a self-mill from an unrelated spell also feeds the croak exile") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val me = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(me, "Grolnok, the Omnivore")

        // Mental Note: "Mill two cards. Draw a card." — nothing to do with a Frog attacking.
        d.putCardOnTopOfLibrary(me, "Lightning Bolt")
        val bear = d.putCardOnTopOfLibrary(me, "Grizzly Bears")

        val note = d.putCardInHand(me, "Mental Note")
        d.giveMana(me, Color.BLUE, 1)
        d.castSpell(me, note).isSuccess shouldBe true
        repeat(4) { d.bothPass() }

        withClue("the milled creature card is exiled with a croak counter, the instant is not") {
            d.getExileCardNames(me) shouldBe listOf("Grizzly Bears")
            d.getGraveyardCardNames(me).shouldContainExactlyInAnyOrder("Lightning Bolt", "Mental Note")
            croakCounters(d, bear) shouldBe 1
        }
    }

    test("only croak-countered cards are playable from exile") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val me = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        d.putCreatureOnBattlefield(me, "Grolnok, the Omnivore")

        val croaked = d.putCardOnTopOfLibrary(me, "Island")
        d.putCardOnTopOfLibrary(me, "Mountain") // second milled card, also croak-exiled
        val note = d.putCardInHand(me, "Mental Note")
        d.giveMana(me, Color.BLUE, 1)
        d.castSpell(me, note).isSuccess shouldBe true
        repeat(4) { d.bothPass() }
        croakCounters(d, croaked) shouldBe 1

        // A card put into exile without a croak counter is outside the permission's filter.
        val plainExile = d.putCardInExile(me, "Forest")

        val landPlays = d.legalActions(me)
            .mapNotNull { it.action as? PlayLand }
            .map { it.cardId }
            .toSet()

        withClue("the croak-countered land is offered as a land drop from exile") {
            landPlays.contains(croaked) shouldBe true
        }
        withClue("a land exiled without a croak counter is not") {
            landPlays.contains(plainExile) shouldBe false
        }
    }
})
