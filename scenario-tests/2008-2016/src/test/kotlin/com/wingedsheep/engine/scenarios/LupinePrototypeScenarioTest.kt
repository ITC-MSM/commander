package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Lupine Prototype (EMN #197) — {2} Artifact Creature — Wolf Construct, 5/5.
 *
 * "This creature can't attack or block unless a player has no cards in hand."
 *
 * "A player" is existential over *every* player, not just controller/opponent — modeled with
 * `Compare(DynamicAmount.CountPlayersWith(Player.Each, Conditions.EmptyHand), GTE, Fixed(1))`
 * (`Exists(Player.Any, Zone.HAND, negate = true)` is the wrong shape here: negating the whole
 * existential means "no player has a card," i.e. *every* hand empty, not "some" hand empty).
 * This test exercises both restrictions (attack and block) and confirms the condition is satisfied
 * when *either* player (not just the creature's controller) is empty-handed.
 */
class LupinePrototypeScenarioTest : ScenarioTestBase() {

    init {
        context("Lupine Prototype — can't attack or block unless a player has an empty hand") {

            test("cannot attack while both players hold cards") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Lupine Prototype", summoningSickness = false)
                    .withCardInHand(1, "Grizzly Bears")
                    .withCardInHand(2, "Hill Giant")
                    .withActivePlayer(1)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)

                withClue("both players have cards in hand — the attack must be rejected") {
                    game.declareAttackers(mapOf("Lupine Prototype" to 2)).error shouldNotBe null
                }
            }

            test("can attack when the controller's own hand is empty") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Lupine Prototype", summoningSickness = false)
                    .withCardInHand(2, "Hill Giant")
                    .withActivePlayer(1)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)

                withClue("controller's hand is empty — the attack is legal") {
                    game.declareAttackers(mapOf("Lupine Prototype" to 2)).error shouldBe null
                }
            }

            test("can attack when only the OPPONENT's hand is empty (existential over any player)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Lupine Prototype", summoningSickness = false)
                    .withCardInHand(1, "Grizzly Bears")
                    // Player2's hand is empty.
                    .withActivePlayer(1)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)

                withClue("opponent's hand is empty — Player.Any is satisfied and the attack is legal") {
                    game.declareAttackers(mapOf("Lupine Prototype" to 2)).error shouldBe null
                }
            }

            test("cannot block while both players hold cards") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(2, "Lupine Prototype", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInHand(1, "Hill Giant")
                    .withCardInHand(2, "Doom Blade")
                    .withActivePlayer(1)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

                withClue("both players have cards in hand — the block must be rejected") {
                    game.declareBlockers(mapOf("Lupine Prototype" to listOf("Grizzly Bears"))).error shouldNotBe null
                }
            }

            test("can block once a player's hand is empty") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(2, "Lupine Prototype", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    // Neither player has cards in hand.
                    .withActivePlayer(1)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

                withClue("an empty hand satisfies the condition — the block is legal") {
                    game.declareBlockers(mapOf("Lupine Prototype" to listOf("Grizzly Bears"))).error shouldBe null
                }
            }
        }
    }
}
