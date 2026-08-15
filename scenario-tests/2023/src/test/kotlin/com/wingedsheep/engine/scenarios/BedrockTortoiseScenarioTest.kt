package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Bedrock Tortoise (LCI #176) — {3}{G} Creature — Turtle 0/6 (rare).
 *
 * "During your turn, creatures you control have hexproof."
 * "Each creature you control with toughness greater than its power assigns combat damage equal to
 *  its toughness rather than its power."
 *
 * Two statics under test:
 *  1. Conditional hexproof grant — [ConditionalStaticAbility] with [IsYourTurn] gates
 *     [GrantKeyword](HEXPROOF, AllCreaturesYouControl). Creatures you control have hexproof only
 *     during your turn; on the opponent's turn the grant is inactive.
 *  2. Assign damage by toughness — [AssignDamageEqualToToughness](AllCreaturesYouControl,
 *     onlyWhenToughnessGreaterThanPower = true). The Tortoise itself (0/6) satisfies toughness (6)
 *     > power (0) and therefore deals 6 unblocked combat damage; a 2/2 (Grizzly Bears) does not
 *     satisfy the condition (2 == 2) and continues to deal damage equal to its power (2).
 */
class BedrockTortoiseScenarioTest : ScenarioTestBase() {

    init {

        context("Bedrock Tortoise — hexproof during your turn") {

            test("creatures you control have hexproof during your turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Bedrock Tortoise", tapped = false, summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", tapped = false, summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val tortoise = game.findPermanent("Bedrock Tortoise")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                val projected = game.state.projectedState

                withClue("Bedrock Tortoise itself has hexproof during its controller's turn") {
                    projected.hasKeyword(tortoise, Keyword.HEXPROOF) shouldBe true
                }
                withClue("Grizzly Bears you control also gain hexproof during your turn") {
                    projected.hasKeyword(bears, Keyword.HEXPROOF) shouldBe true
                }
            }

            test("creatures you control do not have hexproof during the opponent's turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Bedrock Tortoise", tapped = false, summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", tapped = false, summoningSickness = false)
                    .withActivePlayer(2) // Player2's turn — IsYourTurn is false for Player1
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val tortoise = game.findPermanent("Bedrock Tortoise")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                val projected = game.state.projectedState

                withClue("Bedrock Tortoise does not have hexproof on the opponent's turn") {
                    projected.hasKeyword(tortoise, Keyword.HEXPROOF) shouldBe false
                }
                withClue("Grizzly Bears you control do not have hexproof on the opponent's turn") {
                    projected.hasKeyword(bears, Keyword.HEXPROOF) shouldBe false
                }
            }
        }

        context("Bedrock Tortoise — assigns combat damage equal to toughness") {

            test("Bedrock Tortoise (0/6) attacks unblocked and deals 6 damage (its toughness)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Bedrock Tortoise", tapped = false, summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val attack = game.declareAttackers(mapOf("Bedrock Tortoise" to 2))
                withClue("Bedrock Tortoise should be able to attack: ${attack.error}") {
                    attack.error shouldBe null
                }

                // Player2 declares no blockers; combat damage auto-resolves.
                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                withClue("Player2 took 6 combat damage — Tortoise's toughness (6), not its power (0)") {
                    game.getLifeTotal(2) shouldBe 14
                }
            }

            test("creature with toughness equal to power (Grizzly Bears 2/2) still deals power damage") {
                // Grizzly Bears is 2/2 — toughness (2) is NOT greater than power (2), so the static
                // does not apply and it deals damage equal to its power as usual (2).
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Bedrock Tortoise", tapped = false, summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", tapped = false, summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val attack = game.declareAttackers(mapOf("Grizzly Bears" to 2))
                withClue("Grizzly Bears should be able to attack: ${attack.error}") {
                    attack.error shouldBe null
                }

                game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

                withClue("Grizzly Bears (2/2) dealt 2 damage equal to its power, not its toughness") {
                    game.getLifeTotal(2) shouldBe 18
                }
            }
        }
    }
}
