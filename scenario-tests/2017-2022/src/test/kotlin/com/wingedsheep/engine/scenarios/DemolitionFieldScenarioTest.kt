package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Demolition Field (BRO #260, reprinted FDN #687).
 *
 * "{T}: Add {C}.
 *  {2}, {T}, Sacrifice this land: Destroy target nonbasic land an opponent controls. That land's
 *  controller may search their library for a basic land card, put it onto the battlefield, then
 *  shuffle. You may search your library for a basic land card, put it onto the battlefield, then
 *  shuffle."
 *
 * Verifies the whole chain: the targeted nonbasic land dies, *its controller* (the opponent, via
 * [Player.ControllerOf]) fetches a basic onto the battlefield, and the activating player fetches
 * one too.
 */
class DemolitionFieldScenarioTest : ScenarioTestBase() {

    private fun forestsControlledBy(game: TestGame, playerId: EntityId): Int =
        game.state.getBattlefield(playerId).count { id ->
            game.state.getEntity(id)?.get<CardComponent>()?.name == "Forest"
        }

    init {
        context("Demolition Field") {

            test("destroys the opponent's nonbasic land; both its controller and you fetch a basic") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Demolition Field")
                    // Two Forests to pay the {2} in the sacrifice ability's cost.
                    .withLandsOnBattlefield(1, "Forest", 2)
                    // The opponent's nonbasic land is the target.
                    .withCardOnBattlefield(2, "Strip Mine")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                // Both libraries hold only basics, so any search selection is a basic land.
                repeat(6) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(6) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val p1 = game.player1Id
                val p2 = game.player2Id
                withClue("Precondition: opponent controls no Forests yet") {
                    forestsControlledBy(game, p2) shouldBe 0
                }
                val p1ForestsBefore = forestsControlledBy(game, p1)

                val field = game.findPermanent("Demolition Field")!!
                val stripMine = game.findPermanent("Strip Mine")!!
                val sacAbilityId = cardRegistry.getCard("Demolition Field")!!.script.activatedAbilities[1].id

                val result = game.execute(
                    ActivateAbility(
                        playerId = p1,
                        sourceId = field,
                        abilityId = sacAbilityId,
                        targets = listOf(ChosenTarget.Permanent(stripMine)),
                    )
                )
                withClue("Activating the sacrifice ability should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                // Drive the two optional basic-land searches (opponent first, then the controller).
                var guard = 0
                while (game.hasPendingDecision() && guard++ < 12) {
                    when (val d = game.getPendingDecision()) {
                        is YesNoDecision -> game.answerYesNo(true)
                        is SelectCardsDecision -> game.selectCards(listOf(d.options.first()))
                        else -> game.skipSelection()
                    }
                    game.resolveStack()
                }

                withClue("Demolition Field sacrificed itself as a cost") {
                    game.isInGraveyard(1, "Demolition Field") shouldBe true
                }
                withClue("The targeted Strip Mine was destroyed") {
                    game.isInGraveyard(2, "Strip Mine") shouldBe true
                    game.isOnBattlefield("Strip Mine") shouldBe false
                }
                withClue("The destroyed land's controller (the opponent) fetched a basic onto the battlefield") {
                    forestsControlledBy(game, p2) shouldBe 1
                }
                withClue("The activating player also fetched a basic onto the battlefield") {
                    forestsControlledBy(game, p1) shouldBe p1ForestsBefore + 1
                }
            }
        }
    }
}
