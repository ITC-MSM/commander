package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Divergent Equation {X}{X}{U} Instant — "Return up to X target instant and/or sorcery cards from
 * your graveyard to your hand. Exile Divergent Equation."
 *
 * X-clamped graveyard targeting: X chosen at cast time caps how many instant/sorcery cards can be
 * returned, and the spell exiles itself on resolution rather than going to the graveyard.
 */
class DivergentEquationScenarioTest : ScenarioTestBase() {

    private fun TestGame.castDivergentEquation(
        xValue: Int,
        graveyardTargetIds: List<EntityId>,
    ) = execute(
        CastSpell(
            playerId = player1Id,
            cardId = state.getHand(player1Id).first { id ->
                state.getEntity(id)?.get<CardComponent>()?.name == "Divergent Equation"
            },
            targets = graveyardTargetIds.map { ChosenTarget.Card(it, player1Id, Zone.GRAVEYARD) },
            xValue = xValue,
        )
    )

    init {
        context("Divergent Equation — return up to X instant/sorcery cards, then self-exile") {

            test("X=2 returns two targeted instant/sorcery cards to hand and exiles itself") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Divergent Equation")
                    .withCardInGraveyard(1, "Lightning Bolt") // instant
                    .withCardInGraveyard(1, "Divination")     // sorcery
                    .withCardInGraveyard(1, "Grizzly Bears")  // creature — must NOT be targetable
                    .withLandsOnBattlefield(1, "Island", 5)   // {X=2}{X=2}{U} = 5 mana
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bolt = game.findCardsInGraveyard(1, "Lightning Bolt").single()
                val divination = game.findCardsInGraveyard(1, "Divination").single()

                game.castDivergentEquation(xValue = 2, graveyardTargetIds = listOf(bolt, divination))
                    .error shouldBe null
                game.resolveStack()

                withClue("Both targeted instant/sorcery cards return to hand") {
                    game.findCardsInHand(1, "Lightning Bolt").size shouldBe 1
                    game.findCardsInHand(1, "Divination").size shouldBe 1
                }
                withClue("The creature card stays in the graveyard (not a legal target)") {
                    game.findCardsInGraveyard(1, "Grizzly Bears").size shouldBe 1
                }
                withClue("Divergent Equation exiles itself instead of going to the graveyard") {
                    game.findCardsInGraveyard(1, "Divergent Equation").size shouldBe 0
                    (game.state.getExile(game.player1Id).any { id ->
                        game.state.getEntity(id)?.get<CardComponent>()?.name == "Divergent Equation"
                    }) shouldBe true
                }
            }

            test("X=1 with no targets chosen returns nothing (up to X) and still self-exiles") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Divergent Equation")
                    .withCardInGraveyard(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Island", 3) // {X=1}{X=1}{U} = 3 mana
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castDivergentEquation(xValue = 1, graveyardTargetIds = emptyList())
                    .error shouldBe null
                game.resolveStack()

                withClue("No target chosen → Lightning Bolt stays in the graveyard") {
                    game.findCardsInGraveyard(1, "Lightning Bolt").size shouldBe 1
                    game.findCardsInHand(1, "Lightning Bolt").size shouldBe 0
                }
                withClue("Divergent Equation still exiles itself") {
                    (game.state.getExile(game.player1Id).any { id ->
                        game.state.getEntity(id)?.get<CardComponent>()?.name == "Divergent Equation"
                    }) shouldBe true
                }
            }
        }
    }
}
