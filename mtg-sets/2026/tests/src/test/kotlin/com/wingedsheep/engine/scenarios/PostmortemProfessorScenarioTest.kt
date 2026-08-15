package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Postmortem Professor (Secrets of Strixhaven #93).
 *
 * Postmortem Professor ({1}{B}, 2/2, Zombie Warlock):
 *   This creature can't block.
 *   Whenever this creature attacks, each opponent loses 1 life and you gain 1 life.
 *   {1}{B}, Exile an instant or sorcery card from your graveyard:
 *     Return this card from your graveyard to the battlefield.
 *
 * Exercises the attack drain trigger and the graveyard-activated recursion whose cost combines mana
 * with exiling an instant or sorcery from the graveyard.
 */
class PostmortemProfessorScenarioTest : ScenarioTestBase() {

    init {
        context("Postmortem Professor — attack drain + graveyard recursion") {

            test("attacking drains each opponent 1 and gains you 1 life") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Postmortem Professor", summoningSickness = false)
                    .withLifeTotal(1, 20)
                    .withLifeTotal(2, 20)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Postmortem Professor" to 2))
                game.resolveStack()

                withClue("You gain 1 life on attack") { game.getLifeTotal(1) shouldBe 21 }
                withClue("Each opponent loses 1 life on attack") { game.getLifeTotal(2) shouldBe 19 }
            }

            test("graveyard ability exiles an instant/sorcery and returns this card to the battlefield") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInGraveyard(1, "Postmortem Professor")
                    .withCardInGraveyard(1, "Giant Growth") // an instant card to exile as the cost
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("Starts in the graveyard, not on the battlefield") {
                    game.isInGraveyard(1, "Postmortem Professor") shouldBe true
                    game.isOnBattlefield("Postmortem Professor") shouldBe false
                }

                val graveyardCard = game.state.getGraveyard(game.player1Id).first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Postmortem Professor"
                }
                val abilityId = cardRegistry.getCard("Postmortem Professor")!!
                    .activatedAbilities.first().id

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = graveyardCard,
                        abilityId = abilityId,
                    )
                )
                withClue("Activating the graveyard ability should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("Postmortem Professor returns to the battlefield") {
                    game.isOnBattlefield("Postmortem Professor") shouldBe true
                }
                withClue("The exiled instant left the graveyard for exile") {
                    game.isInGraveyard(1, "Giant Growth") shouldBe false
                    game.state.getZone(com.wingedsheep.engine.state.ZoneKey(game.player1Id, Zone.EXILE))
                        .any { game.state.getEntity(it)?.get<CardComponent>()?.name == "Giant Growth" } shouldBe true
                }
            }

            test("graveyard ability is not offered without an instant or sorcery to exile") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInGraveyard(1, "Postmortem Professor")
                    .withCardInGraveyard(1, "Grizzly Bears") // a creature — not a valid exile target
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(2, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val graveyardCard = game.state.getGraveyard(game.player1Id).first {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Postmortem Professor"
                }
                val abilityId = cardRegistry.getCard("Postmortem Professor")!!
                    .activatedAbilities.first().id

                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = graveyardCard,
                        abilityId = abilityId,
                    )
                )
                withClue("Without an instant/sorcery in the graveyard the ability can't be paid for") {
                    (result.error != null) shouldBe true
                    game.isOnBattlefield("Postmortem Professor") shouldBe false
                }
            }
        }
    }
}
