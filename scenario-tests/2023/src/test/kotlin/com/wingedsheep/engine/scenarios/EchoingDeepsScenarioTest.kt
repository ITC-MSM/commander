package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

/**
 * Echoing Deeps (LCI #271) — Land — Cave
 * "You may have this land enter tapped as a copy of any land card in a graveyard, except it's a Cave
 *  in addition to its other types.
 *  {T}: Add {C}."
 *
 * Exercises the [com.wingedsheep.sdk.scripting.EntersAsCopy] replacement wired into the direct
 * land-play entry path, with the "enter tapped as a copy" rider (`tappedIfCopied`) and the extra
 * "Cave" subtype. Covers: copy chosen (CR 707.2), copy declined ("may"), and no candidate present.
 */
class EchoingDeepsScenarioTest : ScenarioTestBase() {

    private fun com.wingedsheep.engine.support.ScenarioTestBase.TestGame.handCardId(name: String) =
        state.getHand(player1Id).first { id ->
            state.getEntity(id)?.get<CardComponent>()?.name == name
        }

    init {
        context("Echoing Deeps — enters as a copy of a graveyard land") {

            test("choosing a graveyard land copies it, adds Cave, and enters tapped") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Echoing Deeps")
                    .withCardInGraveyard(1, "Island")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val deeps = game.handCardId("Echoing Deeps")
                val played = game.execute(PlayLand(game.player1Id, deeps))
                withClue("Playing Echoing Deeps should succeed: ${played.error}") { played.error shouldBe null }

                withClue("An EntersAsCopy selection should be pending") {
                    game.hasPendingDecision().shouldBeTrue()
                }

                // Choose the graveyard Island to copy.
                val islandInGrave = game.state.getGraveyard(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Island"
                }
                val resolved = game.selectCards(listOf(islandInGrave))
                withClue("Resolving the copy choice should succeed: ${resolved.error}") { resolved.error shouldBe null }

                val land = game.state.getBattlefield(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Island"
                }
                withClue("Copy took the Island's name") {
                    game.state.getEntity(land)?.get<CardComponent>()?.name shouldBe "Island"
                }
                val subtypes = game.state.projectedState.getSubtypes(land)
                withClue("Copy keeps Island's subtype and gains Cave (subtypes=$subtypes)") {
                    subtypes.any { it.equals("Island", ignoreCase = true) }.shouldBeTrue()
                    subtypes.any { it.equals("Cave", ignoreCase = true) }.shouldBeTrue()
                }
                withClue("Enters tapped when it enters as a copy") {
                    game.state.getEntity(land)?.has<TappedComponent>()?.shouldBeTrue()
                }
            }

            test("declining the copy enters untapped as a Cave named Echoing Deeps") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Echoing Deeps")
                    .withCardInGraveyard(1, "Island")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val deeps = game.handCardId("Echoing Deeps")
                game.execute(PlayLand(game.player1Id, deeps)).error shouldBe null
                game.hasPendingDecision().shouldBeTrue()

                val resolved = game.skipSelection()
                withClue("Declining should succeed: ${resolved.error}") { resolved.error shouldBe null }

                val land = game.state.getBattlefield(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Echoing Deeps"
                }
                withClue("Stays Echoing Deeps (its printed self)") {
                    game.state.getEntity(land)?.get<CardComponent>()?.name shouldBe "Echoing Deeps"
                }
                val subtypes = game.state.projectedState.getSubtypes(land)
                withClue("Still a Cave, not an Island (subtypes=$subtypes)") {
                    subtypes.any { it.equals("Cave", ignoreCase = true) }.shouldBeTrue()
                    subtypes.any { it.equals("Island", ignoreCase = true) }.shouldBeFalse()
                }
                withClue("Enters untapped when the copy is declined") {
                    game.state.getEntity(land)?.has<TappedComponent>()?.shouldBeFalse()
                }
            }

            test("no land in any graveyard enters normally with no decision") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Echoing Deeps")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val deeps = game.handCardId("Echoing Deeps")
                val played = game.execute(PlayLand(game.player1Id, deeps))
                withClue("Playing should succeed: ${played.error}") { played.error shouldBe null }
                withClue("No copy candidate, so no decision is presented") {
                    game.hasPendingDecision().shouldBeFalse()
                }

                val land = game.state.getBattlefield(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Echoing Deeps"
                }
                withClue("Enters untapped as its printed self") {
                    game.state.getEntity(land)?.has<TappedComponent>()?.shouldBeFalse()
                }
            }
        }
    }
}
