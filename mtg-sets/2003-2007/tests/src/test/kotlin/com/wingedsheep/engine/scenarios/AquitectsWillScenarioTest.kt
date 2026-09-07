package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.AquitectsWill
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Aquitect's Will — "Put a flood counter on target land. That land is an Island in addition to its
 * other types for as long as it has a flood counter on it. If you control a Merfolk, draw a card."
 *
 * The Island half is the part worth proving. Every other card in the corpus that turns a flooded
 * land into an Island (Quicksilver Fountain, Eluge) does it with a *global static borne by a
 * permanent*, which stops applying when that permanent leaves. Aquitect's Will is a sorcery: by the
 * time anyone cares it is in the graveyard, so the effect has to be a source-independent floating
 * one gated on the counter. These tests pin the three ways that distinction shows up:
 *
 *  - **In addition to**, not instead of — the land keeps its printed type and mana ability, and
 *    gains the Island's `{T}: Add {U}` (CR 305.6).
 *  - **It outlives the sorcery and the turn** — a duration bug would make it expire in cleanup.
 *  - **The counter is the gate** — remove it and the land stops being an Island (CR 611.2b).
 */
class AquitectsWillScenarioTest : FunSpec({

    // Forced during spec construction rather than in the first test body: `TestCards.all` scans the
    // whole card corpus, and paying that under a per-test timeout is what flakes a single-spec run.
    val cards = TestCards.all + AquitectsWill

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(cards)
        d.initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    // Projected subtype casing is not normalized across the layer system, so compare lowercased.
    fun GameTestDriver.subtypes(land: EntityId): Set<String> =
        state.projectedState.getSubtypes(land).map { it.lowercase() }.toSet()

    fun GameTestDriver.floodCounters(land: EntityId): Int =
        state.getEntity(land)?.get<CountersComponent>()?.getCount(CounterType.FLOOD) ?: 0

    /** Cast the Will at [land], resolving it. */
    fun GameTestDriver.castWill(land: EntityId) {
        val spell = putCardInHand(player1, "Aquitect's Will")
        giveMana(player1, Color.BLUE, 1)
        castSpell(player1, spell, listOf(land)).error shouldBe null
        bothPass().error shouldBe null
    }

    test("the flooded land becomes an Island in addition to its other types") {
        val d = driver()
        val mountain = d.putLandOnBattlefield(d.player1, "Mountain")

        d.castWill(mountain)

        d.floodCounters(mountain) shouldBe 1
        withClue("\"in addition to its other types\" — the Mountain is not replaced") {
            d.subtypes(mountain) shouldBe setOf("mountain", "island")
        }
    }

    test("an opponent's land is a legal target too — the card says target land, not one you control") {
        val d = driver()
        val theirs = d.putLandOnBattlefield(d.player2, "Forest")

        d.castWill(theirs)

        d.floodCounters(theirs) shouldBe 1
        d.subtypes(theirs) shouldBe setOf("forest", "island")
    }

    test("the land stays an Island after the sorcery is in the graveyard and the turn has ended") {
        val d = driver()
        val mountain = d.putLandOnBattlefield(d.player1, "Mountain")

        d.castWill(mountain)
        d.assertInGraveyard(d.player1, "Aquitect's Will")

        // Roll into the opponent's turn: a duration bug (EndOfTurn instead of the counter gate)
        // would have cleanup strip the Island here.
        d.passPriorityUntil(Step.END)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        withClue("\"for as long as it has a flood counter\" is not \"until end of turn\"") {
            d.subtypes(mountain) shouldBe setOf("mountain", "island")
        }
    }

    test("removing the flood counter ends the Island-ness") {
        val d = driver()
        val mountain = d.putLandOnBattlefield(d.player1, "Mountain")

        d.castWill(mountain)
        d.subtypes(mountain) shouldBe setOf("mountain", "island")

        d.addComponent(mountain, CountersComponent(emptyMap()))

        withClue("the counter is the gate, so the type change goes with it (CR 611.2b)") {
            d.subtypes(mountain) shouldBe setOf("mountain")
        }
    }

    test("the draw rider fires only when you control a Merfolk") {
        for (controlsMerfolk in listOf(true, false)) {
            val d = driver()
            val forest = d.putLandOnBattlefield(d.player1, "Forest")
            // The bare tribal noun reads permanents; a Merfolk creature is the ordinary case, and
            // putting it on the *opponent's* side in the negative case proves the "you control"
            // half rather than merely the "a Merfolk exists" half.
            d.putCreatureOnBattlefield(
                if (controlsMerfolk) d.player1 else d.player2,
                "Merfolk of the Pearl Trident"
            )
            val handBefore = d.getHandSize(d.player1)

            d.castWill(forest)

            withClue("controlsMerfolk=$controlsMerfolk") {
                // The Will itself left the hand as it was cast, so the baseline is handBefore.
                d.getHandSize(d.player1) shouldBe handBefore + (if (controlsMerfolk) 1 else 0)
            }
            d.subtypes(forest) shouldBe setOf("forest", "island")
        }
    }
})
