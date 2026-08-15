package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.combat.AttackingComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Bristlepack Sentry (OTJ) — {1}{G} Plant Wolf 3/3, Defender.
 *
 * "As long as you control a creature with power 4 or greater, this creature can attack
 *  as though it didn't have defender."
 *
 * Exercises [com.wingedsheep.sdk.scripting.CanAttackDespiteDefender] gated by a
 * YouControl(Creature.powerAtLeast(4)) condition.
 */
class BristlepackSentryScenarioTest : ScenarioTestBase() {

    init {
        context("Bristlepack Sentry conditional defender attack") {

            test("cannot attack while you control no creature with power 4 or greater") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Bristlepack Sentry", tapped = false, summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val result = game.declareAttackers(mapOf("Bristlepack Sentry" to 2))
                withClue("Defender blocks the attack with no big creature in play") {
                    (result.error != null) shouldBe true
                }
            }

            test("can attack while you control a creature with power 4 or greater") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Bristlepack Sentry", tapped = false, summoningSickness = false)
                    // Force of Nature is 8/8 — satisfies the power-4-or-greater condition.
                    .withCardOnBattlefield(1, "Force of Nature", tapped = false, summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                val sentry = game.findPermanent("Bristlepack Sentry")!!
                val result = game.declareAttackers(mapOf("Bristlepack Sentry" to 2))
                withClue("Sentry can attack despite Defender: ${result.error}") {
                    result.error shouldBe null
                }
                withClue("Sentry is now an attacker") {
                    game.state.getEntity(sentry)?.get<AttackingComponent>().shouldNotBeNull()
                }
            }
        }
    }
}
