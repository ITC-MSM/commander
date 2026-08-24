package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.DoubleFacedComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Riverglide Pathway // Lavaglide Pathway (ZNR) — the modal double-faced **land**.
 *
 * CR 712.12: *"A player playing a modal double-faced card or a copy of a modal double-faced card
 * as a land chooses one of its faces that's a land before putting it onto the battlefield. It
 * enters the battlefield with that face up."*
 *
 * So one card in hand is two land plays, the choice is taken on the way in, and it is final —
 * CR 712.9 excludes modal DFCs from transforming, so nothing turns the permanent over afterwards.
 * Each test below pins one of those clauses.
 */
class RiverglidePathwayScenarioTest : ScenarioTestBase() {

    private fun manaAbilityIdOf(faceName: String) =
        cardRegistry.requireCard(faceName).script.activatedAbilities.first().id

    private fun newGame() = scenario()
        .withPlayers("Player1", "Player2")
        .withCardInHand(1, "Riverglide Pathway")
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        .build()

    init {
        test("one card in hand offers a land play for each face") {
            val game = newGame()

            val landPlays = game.getLegalActions(1).filter { it.actionType == "PlayLand" }
            withClue("Both faces should be offered, each named for the face it plays") {
                landPlays.map { it.description }.toSet() shouldBe
                    setOf("Play Riverglide Pathway", "Play Lavaglide Pathway")
            }
        }

        test("played front face up: it is Riverglide Pathway and taps for blue") {
            val game = newGame()
            val cardId = game.findCardsInHand(1, "Riverglide Pathway").single()

            game.execute(PlayLand(game.player1Id, cardId)).error shouldBe null

            val land = game.findPermanent("Riverglide Pathway")
            land.shouldNotBeNull()
            game.state.getEntity(land)?.get<DoubleFacedComponent>()?.currentFace shouldBe
                DoubleFacedComponent.Face.FRONT

            game.execute(
                ActivateAbility(game.player1Id, land, manaAbilityIdOf("Riverglide Pathway"))
            ).error shouldBe null
            val pool = game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()
            pool?.getAmount(Color.BLUE) shouldBe 1
            pool?.getAmount(Color.RED) shouldBe 0
        }

        test("played back face up: it is Lavaglide Pathway and taps for red") {
            val game = newGame()
            val cardId = game.findCardsInHand(1, "Riverglide Pathway").single()

            game.execute(PlayLand(game.player1Id, cardId, asBackFace = true)).error shouldBe null

            withClue("The permanent is the back face, not the printed front (CR 712.8f)") {
                game.findPermanent("Riverglide Pathway") shouldBe null
            }
            val land = game.findPermanent("Lavaglide Pathway")
            land.shouldNotBeNull()
            game.state.getEntity(land)?.get<DoubleFacedComponent>()?.currentFace shouldBe
                DoubleFacedComponent.Face.BACK

            game.execute(
                ActivateAbility(game.player1Id, land, manaAbilityIdOf("Lavaglide Pathway"))
            ).error shouldBe null
            val pool = game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()
            pool?.getAmount(Color.RED) shouldBe 1
            pool?.getAmount(Color.BLUE) shouldBe 0
        }

        test("either face spends the same single land drop") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Riverglide Pathway")
                .withCardInHand(1, "Island")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val pathway = game.findCardsInHand(1, "Riverglide Pathway").single()
            game.execute(PlayLand(game.player1Id, pathway, asBackFace = true)).error shouldBe null

            val island = game.findCardsInHand(1, "Island").single()
            withClue("The back-face play is still a land play — the drop is gone") {
                game.execute(PlayLand(game.player1Id, island)).error shouldNotBe null
            }
            game.getLegalActions(1).none { it.actionType == "PlayLand" } shouldBe true
        }

        test("the played face is final — a modal DFC never transforms (CR 712.9)") {
            val game = newGame()
            val cardId = game.findCardsInHand(1, "Riverglide Pathway").single()
            game.execute(PlayLand(game.player1Id, cardId, asBackFace = true))

            val land = game.findPermanent("Lavaglide Pathway")!!
            withClue("Nothing on either face can turn it back over") {
                cardRegistry.requireCard("Lavaglide Pathway").script.activatedAbilities
                    .none { it.effect.description.contains("ransform") } shouldBe true
                game.state.getEntity(land)?.get<CardComponent>()?.name shouldBe "Lavaglide Pathway"
            }
        }

        test("off the battlefield it is the front face again (CR 712.8a)") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Riverglide Pathway")
                .withCardInHand(1, "Stone Rain")
                .withLandsOnBattlefield(1, "Mountain", 3)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val cardId = game.findCardsInHand(1, "Riverglide Pathway").single()
            game.execute(PlayLand(game.player1Id, cardId, asBackFace = true)).error shouldBe null
            val land = game.findPermanent("Lavaglide Pathway")
            land.shouldNotBeNull()

            // Player 1 blows up their own Pathway — a sorcery in their own main phase, so no
            // priority juggling is needed to observe what hits the graveyard.
            game.castSpell(1, "Stone Rain", land).error shouldBe null
            game.resolveStack()

            withClue("A modal DFC in a zone other than battlefield/stack has only its front face's characteristics") {
                game.isInGraveyard(1, "Riverglide Pathway") shouldBe true
                game.isInGraveyard(1, "Lavaglide Pathway") shouldBe false
            }
        }
    }
}
