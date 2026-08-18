package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mrd.cards.SpoilsOfTheVault
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Spoils of the Vault (MRD #78) — "Choose a card name. Reveal cards from the top of your library
 * until you reveal a card with that name, then put that card into your hand. Exile all other cards
 * revealed this way, and you lose 1 life for each of the exiled cards."
 *
 * The whole card is one partition and the life total is the audit of it. The named card must land in
 * hand and *not* in the exile pile, because the pile is the life payment — an off-by-one there is a
 * point of life, invisible on any board where you weren't at exactly lethal. So the three tests are
 * the three sizes that partition can take: some cards above the hit (pay for them), zero cards above
 * it (pay nothing), and no hit at all, where the walk runs the library out and the pile is the whole
 * deck. That last one is the 2018-12-07 ruling and needs no branch in the card — it only works if
 * the "stop here" filter and the "this is the one" filter are the same filter.
 */
class SpoilsOfTheVaultScenarioTest : FunSpec({

    /** Player 1's precombat main with a black mana floating and Spoils in hand. */
    fun driver(startingLife: Int = 20): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + SpoilsOfTheVault)
        d.initMirrorMatch(
            deck = Deck.of("Swamp" to 30, "Forest" to 30),
            startingLife = startingLife,
            startingPlayer = 0
        )
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    /** Cast Spoils and answer its "name a card" prompt with [cardName]. */
    fun GameTestDriver.castSpoilsNaming(cardName: String) {
        val spoils = putCardInHand(player1, "Spoils of the Vault")
        giveMana(player1, Color.BLACK, 1)
        castSpell(player1, spoils)
        bothPass()

        val decision = pendingDecision
        decision shouldNotBe null
        decision as ChooseOptionDecision
        val index = decision.options.indexOf(cardName)
        withClue("\"$cardName\" must be offered — any card name is a legal choice") {
            index shouldNotBe -1
        }
        submitDecision(player1, OptionChosenResponse(decision.id, index))
    }

    fun GameTestDriver.library(): List<EntityId> = state.getZone(ZoneKey(player1, Zone.LIBRARY))

    test("the named card goes to hand and the cards above it are exiled for a life each") {
        val d = driver()

        // top -> Grizzly Bears, Lightning Bolt, Centaur Courser (the name), then the deck.
        d.putCardOnTopOfLibrary(d.player1, "Centaur Courser")
        d.putCardOnTopOfLibrary(d.player1, "Lightning Bolt")
        d.putCardOnTopOfLibrary(d.player1, "Grizzly Bears")

        d.castSpoilsNaming("Centaur Courser")
        d.isPaused shouldBe false

        d.findCardsInHand(d.player1, "Centaur Courser").size shouldBe 1
        withClue("the two cards seen on the way down are the exile pile") {
            d.getExileCardNames(d.player1).sorted() shouldContainExactly
                listOf("Grizzly Bears", "Lightning Bolt")
        }
        withClue("one life per exiled card — the named card is not in that pile") {
            d.getLifeTotal(d.player1) shouldBe 18
        }
    }

    test("naming the top card costs no life") {
        val d = driver()

        d.putCardOnTopOfLibrary(d.player1, "Grizzly Bears")
        d.putCardOnTopOfLibrary(d.player1, "Lightning Bolt")

        d.castSpoilsNaming("Lightning Bolt")
        d.isPaused shouldBe false

        d.findCardsInHand(d.player1, "Lightning Bolt").size shouldBe 1
        withClue("the reveal stopped on the first card, so nothing was exiled") {
            d.getExileCardNames(d.player1) shouldContainExactly emptyList()
            d.getLifeTotal(d.player1) shouldBe 20
        }
    }

    test("naming a card that isn't in the library exiles the whole library (2018-12-07 ruling)") {
        // 30 life so the payment lands without ending the game — the point is the count, not a loss.
        val d = driver(startingLife = 30)

        // A five-card library, none of them the named card.
        val libraryZone = ZoneKey(d.player1, Zone.LIBRARY)
        d.replaceState(d.state.copy(zones = d.state.zones + (libraryZone to emptyList())))
        repeat(3) { d.putCardOnTopOfLibrary(d.player1, "Grizzly Bears") }
        repeat(2) { d.putCardOnTopOfLibrary(d.player1, "Swamp") }
        d.library().size shouldBe 5

        d.castSpoilsNaming("Force of Nature")
        d.isPaused shouldBe false

        withClue("no match means the walk runs the library out and every card revealed is 'other'") {
            d.library() shouldContainExactly emptyList()
            d.getExileCardNames(d.player1).size shouldBe 5
            d.findCardsInHand(d.player1, "Force of Nature").size shouldBe 0
        }
        withClue("one life per exiled card: 30 - 5") {
            d.getLifeTotal(d.player1) shouldBe 25
        }
    }
})
