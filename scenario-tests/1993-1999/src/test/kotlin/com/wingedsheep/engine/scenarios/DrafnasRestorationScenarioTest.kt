package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.core.OrderedResponse
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Drafna's Restoration (ATQ #8).
 *
 * {U} Sorcery — "Put any number of target artifact cards from target player's graveyard on top of
 * their library in any order."
 *
 * Proves: (a) the separately-targeted-player graveyard predicate — `OwnedByTargetPlayer` in
 * `Zone.GRAVEYARD`, now resolvable at target-validation time because TargetValidator threads the
 * chosen player target into graveyard-card validation; and (b) player-chosen ordering of the moved
 * collection onto the library top (`CardOrder.ControllerChooses`).
 */
class DrafnasRestorationScenarioTest : ScenarioTestBase() {

    init {
        context("Drafna's Restoration") {

            fun TestGame.graveyardCard(playerNumber: Int, name: String) =
                state.getGraveyard(if (playerNumber == 1) player1Id else player2Id)
                    .first { state.getEntity(it)?.get<CardComponent>()?.name == name }

            test("puts chosen artifact cards from the targeted player's graveyard onto their library top in the chosen order") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Drafna's Restoration")
                    .withLandsOnBattlefield(1, "Island", 1)
                    // P2's graveyard: two artifacts to return + a nonartifact (must not be targetable)
                    // + an artifact that we leave behind (proves "any number" = we pick a subset).
                    .withCardInGraveyard(2, "Ornithopter")
                    .withCardInGraveyard(2, "Millstone")
                    .withCardInGraveyard(2, "Triskelion")
                    .withCardInGraveyard(2, "Ancestral Recall") // nonartifact control
                    // P2 has an existing library card to confirm the returned cards go ON TOP of it.
                    .withCardInLibrary(2, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val ornithopter = game.graveyardCard(2, "Ornithopter")
                val millstone = game.graveyardCard(2, "Millstone")
                val existingLibTop = game.state.getLibrary(game.player2Id)

                // P1 casts Drafna's Restoration targeting P2 (the player) and two artifact cards in
                // P2's graveyard: Ornithopter and Millstone (leaving Triskelion behind).
                val cardId = game.state.getHand(game.player1Id)
                    .first { game.state.getEntity(it)?.get<CardComponent>()?.name == "Drafna's Restoration" }
                val cast = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = cardId,
                        targets = listOf(
                            ChosenTarget.Player(game.player2Id),
                            ChosenTarget.Card(ornithopter, game.player2Id, Zone.GRAVEYARD),
                            ChosenTarget.Card(millstone, game.player2Id, Zone.GRAVEYARD)
                        )
                    )
                )
                withClue("Casting Drafna's Restoration with a player + two graveyard-artifact targets should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }

                // Resolve; choose to put Millstone on top, then Ornithopter (the player-chosen order).
                game.resolveStack()
                val reorder = game.state.pendingDecision
                withClue("A reorder decision should be pending for the two returned cards") {
                    (reorder != null) shouldBe true
                }
                game.submitDecision(OrderedResponse(reorder!!.id, listOf(millstone, ornithopter)))
                game.resolveStack()

                val library = game.state.getLibrary(game.player2Id)
                withClue("Both chosen artifacts left the graveyard") {
                    game.state.getGraveyard(game.player2Id) shouldNotContainCards listOf(ornithopter, millstone)
                }
                withClue("The two returned cards are on TOP of the library in the chosen order (Millstone, then Ornithopter), above the pre-existing card") {
                    library.take(2) shouldContainExactly listOf(millstone, ornithopter)
                    library.drop(2) shouldContainExactly existingLibTop
                }
                withClue("Triskelion (an artifact we didn't target) stays in the graveyard") {
                    val trisk = game.graveyardCard(2, "Triskelion")
                    game.state.getGraveyard(game.player2Id).contains(trisk) shouldBe true
                }
            }

            test("an artifact card in a player OTHER than the targeted one's graveyard is not a legal target") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Drafna's Restoration")
                    .withLandsOnBattlefield(1, "Island", 1)
                    // The artifact we'll try to pull lives in P1's OWN graveyard...
                    .withCardInGraveyard(1, "Ornithopter")
                    // ...while we point the player target at P2. OwnedByTargetPlayer must reject it.
                    .withCardInGraveyard(2, "Millstone")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val p1Ornithopter = game.graveyardCard(1, "Ornithopter")
                val cardId = game.state.getHand(game.player1Id)
                    .first { game.state.getEntity(it)?.get<CardComponent>()?.name == "Drafna's Restoration" }

                // Player target = P2, but the artifact card target is in P1's graveyard.
                val cast = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = cardId,
                        targets = listOf(
                            ChosenTarget.Player(game.player2Id),
                            ChosenTarget.Card(p1Ornithopter, game.player1Id, Zone.GRAVEYARD)
                        )
                    )
                )
                withClue("A card in a non-targeted player's graveyard must fail OwnedByTargetPlayer validation") {
                    (cast.error != null) shouldBe true
                }
            }
        }
    }

    private infix fun List<com.wingedsheep.sdk.model.EntityId>.shouldNotContainCards(cards: List<com.wingedsheep.sdk.model.EntityId>) {
        cards.forEach { (it in this) shouldBe false }
    }
}
