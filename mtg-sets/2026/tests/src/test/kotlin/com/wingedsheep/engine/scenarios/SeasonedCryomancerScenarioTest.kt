package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.fra.cards.SeasonedCryomancer
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Seasoned Cryomancer (FRA #38) — ETB draw two, discard two; a reflexive trigger taps up to that
 * many target creatures (one per nonland card discarded) and stuns them. Graveyard: {3}{U}{U},
 * exile it: draw two.
 */
class SeasonedCryomancerScenarioTest : ScenarioTestBase() {

    private fun TestGame.stunCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.STUN) ?: 0

    private fun TestGame.isTapped(id: EntityId): Boolean =
        state.getEntity(id)?.has<TappedComponent>() == true

    /** Discards whatever the "discard two" prompt offers, if it prompts at all. */
    private fun TestGame.discardAllOffered() {
        val decision = getPendingDecision()
        if (decision is SelectCardsDecision) {
            selectCards(decision.options).error shouldBe null
        }
    }

    init {
        fun castGame(libraryCard: String): TestGame {
            var b = scenario()
                .withPlayers("Player", "Opponent")
                .withCardInHand(1, "Seasoned Cryomancer")
                .withLandsOnBattlefield(1, "Island", 3)
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withCardOnBattlefield(2, "Glory Seeker")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            repeat(4) { b = b.withCardInLibrary(1, libraryCard) }
            repeat(4) { b = b.withCardInLibrary(2, "Island") }
            val game = b.build()
            game.castSpell(1, "Seasoned Cryomancer").error shouldBe null
            game.resolveStack()
            return game
        }

        context("Seasoned Cryomancer") {
            test("discarding two nonland cards taps and stuns up to two target creatures") {
                val game = castGame("Grizzly Bears")
                game.discardAllOffered()

                val bears = game.findPermanent("Grizzly Bears")!!
                val seeker = game.findPermanent("Glory Seeker")!!
                game.selectTargets(listOf(bears, seeker)).error shouldBe null
                game.resolveStack()

                game.graveyardSize(1) shouldBe 2
                for (id in listOf(bears, seeker)) {
                    game.isTapped(id) shouldBe true
                    game.stunCounters(id) shouldBe 1
                }
            }

            test("the target count is capped by the nonland cards discarded") {
                val game = castGame("Grizzly Bears")
                game.discardAllOffered()

                val bears = game.findPermanent("Grizzly Bears")!!
                val seeker = game.findPermanent("Glory Seeker")!!
                withClue("two nonland discards allow two targets, never three") {
                    val cryomancer = game.findPermanent("Seasoned Cryomancer")!!
                    (game.selectTargets(listOf(bears, seeker, cryomancer)).error != null) shouldBe true
                }
                game.selectTargets(listOf(bears)).error shouldBe null
                game.resolveStack()

                game.stunCounters(bears) shouldBe 1
                game.isTapped(seeker) shouldBe false
            }

            test("discarding only lands creates no reflexive trigger") {
                val game = castGame("Island")
                game.discardAllOffered()
                game.resolveStack()

                withClue("no target prompt follows a land-only discard") {
                    game.hasPendingDecision() shouldBe false
                }
                game.graveyardSize(1) shouldBe 2
                val bears = game.findPermanent("Grizzly Bears")!!
                game.isTapped(bears) shouldBe false
                game.stunCounters(bears) shouldBe 0
            }

            test("from the graveyard, {3}{U}{U} and exiling it draws two cards") {
                var b = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInGraveyard(1, "Seasoned Cryomancer")
                    .withLandsOnBattlefield(1, "Island", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(4) { b = b.withCardInLibrary(1, "Island") }
                repeat(4) { b = b.withCardInLibrary(2, "Island") }
                val game = b.build()

                val card = game.findCardsInGraveyard(1, "Seasoned Cryomancer").single()
                val abilityId = SeasonedCryomancer.activatedAbilities.single().id
                game.execute(ActivateAbility(game.player1Id, card, abilityId)).error shouldBe null
                game.resolveStack()

                game.handSize(1) shouldBe 2
                game.state.getZone(game.player1Id, Zone.EXILE).contains(card) shouldBe true
            }
        }
    }
}
