package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Zul Ashur, Lich Lord (FDN #77).
 *
 * {T}: You may cast target Zombie creature card from your graveyard this turn.
 *
 * Covers the activated ability granting a graveyard-cast permission for the targeted Zombie
 * creature card, paying its normal cost. (Ward is the standard keyword and is exercised elsewhere.)
 */
class ZulAshurLichLordScenarioTest : ScenarioTestBase() {

    init {
        context("Zul Ashur, Lich Lord") {

            test("{T} lets you cast the targeted Zombie creature card from your graveyard this turn") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Zul Ashur, Lich Lord", summoningSickness = false)
                    .withCardInGraveyard(1, "Shepherd of Rot") // a Zombie creature card
                    .withLandsOnBattlefield(1, "Swamp", 2) // pays {1}{B}
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val zul = game.findPermanent("Zul Ashur, Lich Lord")!!
                val zombie = game.state.getGraveyard(game.player1Id).first { id ->
                    game.state.getEntity(id)?.get<CardComponent>()?.name == "Shepherd of Rot"
                }
                val abilityId = cardRegistry.getCard("Zul Ashur, Lich Lord")!!
                    .script.activatedAbilities[0].id

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = zul,
                        abilityId = abilityId,
                        targets = listOf(entityIdToChosenTarget(game.state, zombie)),
                    )
                ).error shouldBe null
                game.resolveStack()

                // Now the Zombie can be cast from the graveyard for its normal cost.
                game.castSpellFromGraveyard(1, "Shepherd of Rot").error shouldBe null
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("Shepherd of Rot resolves onto the battlefield") {
                    game.findPermanent("Shepherd of Rot") shouldNotBe null
                }
            }

            test("without the ability, the Zombie can't be cast from the graveyard") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Zul Ashur, Lich Lord", summoningSickness = false)
                    .withCardInGraveyard(1, "Shepherd of Rot")
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("No permission granted, so casting from the graveyard is illegal") {
                    game.castSpellFromGraveyard(1, "Shepherd of Rot").error shouldNotBe null
                }
            }
        }
    }
}
