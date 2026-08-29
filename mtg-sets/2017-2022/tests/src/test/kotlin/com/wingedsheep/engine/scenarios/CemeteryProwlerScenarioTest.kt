package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.mana.CostCalculator
import com.wingedsheep.engine.state.components.battlefield.LinkedExileComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Cemetery Prowler (VOW #191) — {1}{G}{G} Creature — Wolf, 3/4.
 *
 * "Vigilance
 *  Whenever this creature enters or attacks, exile a card from a graveyard.
 *  Spells you cast cost {1} less to cast for each card type they share with cards exiled with this
 *  creature."
 *
 * `CostReductionSource.SharedCardTypesWithLinkedExile` is the only reduction source whose amount
 * depends on the *spell being cast* rather than on the board, which is why the reduction had to be
 * threaded the caster's `CardDefinition`. The tests that matter are the two the card's own rulings
 * call out: the count is over **card types, not cards** (two exiled creature cards are still {1}),
 * and a spell sharing several types with the pile gets several reductions.
 */
class CemeteryProwlerScenarioTest : ScenarioTestBase() {

    init {
        context("Cemetery Prowler — cost reduction per shared card type") {

            /** Seed a Prowler on player 1's battlefield with [exiled] already linked to it. */
            fun prowlerWithExile(vararg exiled: String): TestGame {
                var builder = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Cemetery Prowler")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                exiled.forEach { builder = builder.withCardInExile(2, it) }
                val game = builder
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val prowler = game.findPermanent("Cemetery Prowler")!!
                val exiledIds: List<EntityId> = game.state.getExile(game.player2Id).toList()
                game.state = game.state.updateEntity(prowler) { container ->
                    container.with(LinkedExileComponent(exiledIds))
                }
                return game
            }

            fun genericCost(game: TestGame, spellName: String): Int =
                CostCalculator(cardRegistry).calculateEffectiveCost(
                    game.state,
                    cardRegistry.requireCard(spellName),
                    game.player1Id,
                ).genericAmount

            test("nothing exiled → no reduction") {
                val game = prowlerWithExile()
                withClue("an empty imprint pile shares nothing with anything") {
                    // Grizzly Bears is {1}{G}: one generic.
                    genericCost(game, "Grizzly Bears") shouldBe 1
                }
            }

            test("an exiled creature card makes creature spells {1} cheaper") {
                val game = prowlerWithExile("Grizzly Bears")
                genericCost(game, "Grizzly Bears") shouldBe 0
            }

            test("an exiled creature card does nothing for a land-only share") {
                val game = prowlerWithExile("Mountain")
                withClue("a creature spell shares no card type with an exiled land") {
                    genericCost(game, "Grizzly Bears") shouldBe 1
                }
            }

            test("two exiled creature cards are still only {1} — types, not cards") {
                // The card's own ruling, and the case that separates counting types from counting
                // cards. A card-counting implementation passes every other test here.
                val game = prowlerWithExile("Grizzly Bears", "Llanowar Elves")
                genericCost(game, "Grizzly Bears") shouldBe 0
            }

            test("a spell sharing two card types with the pile is reduced twice") {
                // Sol Ring is an artifact; Grizzly Bears is a creature. An artifact *creature*
                // spell shares both, so it gets {2} off.
                val game = prowlerWithExile("Grizzly Bears", "Sol Ring")
                withClue("Juggernaut is {4} Artifact Creature — both types are in the pile") {
                    genericCost(game, "Juggernaut") shouldBe 2
                }
            }
        }
    }
}
