package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Nurturing Presence (VOW #26) — {1}{W} Enchantment — Aura
 *
 *   Enchant creature
 *   Enchanted creature has "Whenever a creature you control enters, this creature gets +1/+1 until
 *   end of turn."
 *   When this Aura enters, create a 1/1 white Spirit creature token with flying.
 *
 * Exercises the self-ETB Spirit token and the granted "whenever a creature you control enters, this
 * creature gets +1/+1 until end of turn" ability on the enchanted creature.
 */
class NurturingPresenceScenarioTest : ScenarioTestBase() {

    init {
        context("Nurturing Presence — ETB Spirit token and granted enters-pump") {

            test("entering the battlefield creates a 1/1 white flying Spirit token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInHand(1, "Nurturing Presence")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                game.castSpell(1, "Nurturing Presence", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("a Spirit token is created when the Aura enters") {
                    game.findPermanents("Spirit Token").size shouldBe 1
                }
                val token = game.findPermanents("Spirit Token").first()
                withClue("the token is a 1/1 with flying") {
                    game.state.projectedState.getPower(token) shouldBe 1
                    game.state.projectedState.getToughness(token) shouldBe 1
                    game.state.projectedState.hasKeyword(token, Keyword.FLYING) shouldBe true
                }
            }

            test("a creature you control entering pumps the enchanted creature +1/+1 until end of turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Hill Giant", summoningSickness = false)
                    .withCardAttachedTo(1, "Nurturing Presence", "Hill Giant")
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val giant = game.findPermanent("Hill Giant")!!

                withClue("enchanted creature is at base 3/3 before any creature enters") {
                    game.state.projectedState.getPower(giant) shouldBe 3
                    game.state.projectedState.getToughness(giant) shouldBe 3
                }

                // Cast a creature — it enters under the enchanted creature's controller, firing the
                // granted trigger.
                game.castSpell(1, "Grizzly Bears").error shouldBe null
                game.resolveStack()

                withClue("the enchanted creature got +1/+1 when a creature you control entered") {
                    game.state.projectedState.getPower(giant) shouldBe 4
                    game.state.projectedState.getToughness(giant) shouldBe 4
                }
            }

            test("a creature entering under an OPPONENT's control does not pump the enchanted creature") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Hill Giant", summoningSickness = false)
                    .withCardAttachedTo(1, "Nurturing Presence", "Hill Giant")
                    .withCardInHand(2, "Grizzly Bears")
                    .withLandsOnBattlefield(2, "Forest", 2)
                    .withActivePlayer(2)
                    .withPriorityPlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val giant = game.findPermanent("Hill Giant")!!

                game.castSpell(2, "Grizzly Bears").error shouldBe null
                game.resolveStack()

                withClue("an opponent's creature entering does not satisfy 'a creature you control'") {
                    game.state.projectedState.getPower(giant) shouldBe 3
                    game.state.projectedState.getToughness(giant) shouldBe 3
                }
            }
        }
    }
}
