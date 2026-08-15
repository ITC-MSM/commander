package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Carrion Howler (RAV #79) — {3}{B} Zombie Wolf, 2/2.
 *
 * "Pay 1 life: This creature gets +2/-1 until end of turn."
 *
 * Exercises the mtgish `_Cost = PayLife` activation-cost mapping: activating the ability pays 1 life
 * up front and resolves a +2/-1 ModifyStats on the creature until end of turn.
 */
class CarrionHowlerScenarioTest : ScenarioTestBase() {

    private val pumpAbilityId =
        cardRegistry.getCard("Carrion Howler")!!.activatedAbilities.first().id

    init {
        context("Carrion Howler pay-life pump") {

            test("pays 1 life and gives the creature +2/-1 until end of turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Carrion Howler")
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val howler = game.findPermanent("Carrion Howler")!!

                withClue("Base stats before activation are 2/2") {
                    game.state.projectedState.getPower(howler) shouldBe 2
                    game.state.projectedState.getToughness(howler) shouldBe 2
                }

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = howler,
                        abilityId = pumpAbilityId,
                    )
                )
                withClue("Activating the pay-life ability should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }

                withClue("The 1-life activation cost is paid up front") {
                    game.getLifeTotal(1) shouldBe 19
                }

                game.resolveStack()

                withClue("Carrion Howler is 4/1 after the +2/-1 pump resolves") {
                    game.state.projectedState.getPower(howler) shouldBe 4
                    game.state.projectedState.getToughness(howler) shouldBe 1
                }
            }
        }
    }
}
