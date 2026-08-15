package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Geralf, Visionary Stitcher (VOW #61) — {2}{U} Legendary Creature — Human
 * Wizard, 1/4.
 *
 *   Zombies you control have flying.
 *   {U}, {T}, Sacrifice another nontoken creature: Create an X/X blue Zombie creature token, where
 *   X is the sacrificed creature's toughness.
 *
 * Line 1 is a static lord grant; line 2 sacrifices another nontoken creature and stamps the token's
 * X/X from the sacrificed creature's toughness via last-known information. The created Zombie token,
 * being a Zombie you control, also picks up flying from the lord.
 */
class GeralfVisionaryStitcherScenarioTest : ScenarioTestBase() {

    init {
        context("Geralf, Visionary Stitcher") {

            test("Zombies you control have flying; non-Zombies do not") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Geralf, Visionary Stitcher", summoningSickness = false)
                    .withCardOnBattlefield(1, "Cackling Fiend", summoningSickness = false) // 2/1 Zombie
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)   // 2/2 Bear
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val zombie = game.findPermanent("Cackling Fiend")!!
                val bear = game.findPermanent("Grizzly Bears")!!

                withClue("the Zombie you control gains flying from Geralf") {
                    game.state.projectedState.hasKeyword(zombie, Keyword.FLYING) shouldBe true
                }
                withClue("a non-Zombie creature does not gain flying") {
                    game.state.projectedState.hasKeyword(bear, Keyword.FLYING) shouldBe false
                }
            }

            test("sacrificing a 2/2 makes a 2/2 blue Zombie token that also has flying") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Geralf, Visionary Stitcher", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false) // 2/2 fodder
                    .withLandsOnBattlefield(1, "Island", 1) // pays {U}
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val geralf = game.findPermanent("Geralf, Visionary Stitcher")!!
                val fodder = game.findPermanent("Grizzly Bears")!!
                val abilityId = cardRegistry.getCard("Geralf, Visionary Stitcher")!!
                    .activatedAbilities.first().id

                val activate = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = geralf,
                        abilityId = abilityId,
                        costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(fodder))
                    )
                )
                withClue("activation should succeed: ${activate.error}") {
                    activate.error shouldBe null
                }
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                withClue("the sacrificed fodder went to the graveyard") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }

                val tokens = game.findPermanents("Zombie Token")
                withClue("exactly one Zombie token is created") {
                    tokens.size shouldBe 1
                }
                val token = tokens.first()
                withClue("the token is a 2/2 (X = the sacrificed 2/2's toughness)") {
                    game.state.projectedState.getPower(token) shouldBe 2
                    game.state.projectedState.getToughness(token) shouldBe 2
                }
                withClue("the token is blue") {
                    game.state.getEntity(token)!!
                        .get<com.wingedsheep.engine.state.components.identity.CardComponent>()!!
                        .colors.contains(Color.BLUE) shouldBe true
                }
                withClue("the token is a Zombie, so Geralf's lord grants it flying too") {
                    game.state.projectedState.hasKeyword(token, Keyword.FLYING) shouldBe true
                }
            }
        }
    }
}
