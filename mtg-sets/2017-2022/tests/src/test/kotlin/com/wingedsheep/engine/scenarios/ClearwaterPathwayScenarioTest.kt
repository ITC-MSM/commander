package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.state.components.identity.DoubleFacedComponent
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Clearwater Pathway // Murkwater Pathway (ZNR) — one of the ten Pathways.
 *
 * The cycle's rules work (CR 712.12's play-a-face choice, CR 712.9's no-transform, CR 712.8a's
 * front-face-everywhere-else) is pinned once by [RiverglidePathwayScenarioTest]. What is specific
 * to *this* card is which two faces the single card offers and what colour each one taps for, so
 * that is what this test covers.
 */
class ClearwaterPathwayScenarioTest : ScenarioTestBase() {

    private fun manaAbilityIdOf(faceName: String) =
        cardRegistry.requireCard(faceName).script.activatedAbilities.first().id

    private fun newGame() = scenario()
        .withPlayers("Player1", "Player2")
        .withCardInHand(1, "Clearwater Pathway")
        .withActivePlayer(1)
        .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        .build()

    init {
        test("one card in hand offers a land play for each face") {
            val game = newGame()

            val landPlays = game.getLegalActions(1).filter { it.actionType == "PlayLand" }
            landPlays.map { it.description }.toSet() shouldBe
                setOf("Play Clearwater Pathway", "Play Murkwater Pathway")
        }

        test("played front face up: it is Clearwater Pathway and taps for blue") {
            val game = newGame()
            val cardId = game.findCardsInHand(1, "Clearwater Pathway").single()

            game.execute(PlayLand(game.player1Id, cardId)).error shouldBe null

            val land = game.findPermanent("Clearwater Pathway")
            land.shouldNotBeNull()
            game.state.getEntity(land)?.get<DoubleFacedComponent>()?.currentFace shouldBe
                DoubleFacedComponent.Face.FRONT

            game.execute(
                ActivateAbility(game.player1Id, land, manaAbilityIdOf("Clearwater Pathway"))
            ).error shouldBe null
            val pool = game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()
            pool?.getAmount(Color.BLUE) shouldBe 1
            pool?.getAmount(Color.BLACK) shouldBe 0
        }

        test("played back face up: it is Murkwater Pathway and taps for black") {
            val game = newGame()
            val cardId = game.findCardsInHand(1, "Clearwater Pathway").single()

            game.execute(PlayLand(game.player1Id, cardId, asBackFace = true)).error shouldBe null

            withClue("The permanent is the back face, not the printed front (CR 712.8f)") {
                game.findPermanent("Clearwater Pathway") shouldBe null
            }
            val land = game.findPermanent("Murkwater Pathway")
            land.shouldNotBeNull()
            game.state.getEntity(land)?.get<DoubleFacedComponent>()?.currentFace shouldBe
                DoubleFacedComponent.Face.BACK

            game.execute(
                ActivateAbility(game.player1Id, land, manaAbilityIdOf("Murkwater Pathway"))
            ).error shouldBe null
            val pool = game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()
            pool?.getAmount(Color.BLACK) shouldBe 1
            pool?.getAmount(Color.BLUE) shouldBe 0
        }
    }
}
