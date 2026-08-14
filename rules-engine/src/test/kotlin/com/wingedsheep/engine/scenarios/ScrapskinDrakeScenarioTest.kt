package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scrapskin Drake (AVR #73) — Flying; can block only creatures with flying.
 */
class ScrapskinDrakeScenarioTest : ScenarioTestBase() {

    init {
        context("Scrapskin Drake can only block creatures with flying") {

            test("is a 2/3 flyer") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Scrapskin Drake")
                    .build()

                val drake = game.findPermanent("Scrapskin Drake")!!
                game.state.projectedState.getPower(drake) shouldBe 2
                game.state.projectedState.getToughness(drake) shouldBe 3
                game.state.projectedState.hasKeyword(drake, Keyword.FLYING) shouldBe true
            }

            test("can legally block a flying attacker") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Scrapskin Drake", summoningSickness = false)
                    .withCardOnBattlefield(2, "Birds of Paradise", summoningSickness = false)
                    .withActivePlayer(2)
                    .inPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Birds of Paradise" to 1)).error shouldBe null

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                val result = game.declareBlockers(mapOf("Scrapskin Drake" to listOf("Birds of Paradise")))
                withClue("blocking a flying attacker is legal: ${result.error}") {
                    result.error shouldBe null
                }
            }

            test("cannot legally block a non-flying attacker") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Scrapskin Drake", summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withActivePlayer(2)
                    .inPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
                    .build()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Hill Giant" to 1)).error shouldBe null

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                val result = game.declareBlockers(mapOf("Scrapskin Drake" to listOf("Hill Giant")))
                withClue("blocking a non-flying attacker is illegal: ${result.error}") {
                    result.error shouldNotBe null
                }
            }
        }
    }
}
