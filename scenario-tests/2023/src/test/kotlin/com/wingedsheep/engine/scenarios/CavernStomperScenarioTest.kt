package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ExecutionResult
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.lci.cards.CavernStomper
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario test for Cavern Stomper (LCI #177) — {4}{G}{G} Creature — Dinosaur, 7/7, Common.
 *
 * "When this creature enters, scry 2.
 *  {3}{G}: This creature can't be blocked by creatures with power 2 or less this turn."
 *
 * Focus: the activated evasion ability grants a [com.wingedsheep.sdk.scripting.CantBeBlockedBy]
 * static ability, which lives in `GameState.grantedStaticAbilities` (combat reads it directly, not
 * through the layer system) and so never reaches the projected keyword set that feeds `abilityFlags`.
 * This pins that the grant is surfaced to the client as a "Granted Ability" badge — without it the
 * player has no on-creature indicator that the restriction is active.
 */
class CavernStomperScenarioTest : ScenarioTestBase() {

    init {
        // Cavern Stomper is auto-discovered from the LCI cards package (already in the shared
        // cardRegistry). No explicit registration needed.
        val activateAbilityId = CavernStomper.activatedAbilities.first().id

        context("Cavern Stomper") {

            test("{3}{G} evasion grant is surfaced to the client as a Granted Ability badge") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Cavern Stomper")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val stomper = game.findPermanent("Cavern Stomper")!!

                // Before activation there is no granted-ability badge on the creature.
                withClue("no granted-ability badge before the ability is activated") {
                    game.getClientState(1).cards.getValue(stomper)
                        .activeEffects.none { it.icon == "granted-ability" } shouldBe true
                }

                val result = game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = stomper, abilityId = activateAbilityId)
                )
                withClue("activation should succeed: ${result.error}") { result.error shouldBe null }
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                // The granted restriction is now surfaced as a single badge carrying its description.
                val badges = game.getClientState(1).cards.getValue(stomper)
                    .activeEffects.filter { it.icon == "granted-ability" }
                withClue("exactly one granted-ability badge") { badges.size shouldBe 1 }
                withClue("badge description names the block restriction: ${badges.firstOrNull()?.description}") {
                    (badges.single().description?.contains("power 2 or less") == true) shouldBe true
                }
            }

            // Build a game where Cavern Stomper has activated {3}{G} (granting the "can't be blocked
            // by creatures with power 2 or less" restriction to itself) and is attacking, then declare
            // [blockerName] as a blocker and return the result. Proves the granted CantBeBlockedBy is
            // actually *enforced* in combat — the coverage the badge test above lacked, and the reason
            // the restriction previously no-op'd (CantBeBlockedByRule read only printed statics).
            fun declareBlockOnStomper(blockerName: String): ExecutionResult {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Cavern Stomper")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withCardOnBattlefield(2, "Grizzly Bears") // 2/2 — power 2, caught by the restriction
                    .withCardOnBattlefield(2, "Hill Giant")    // 3/3 — power 3, unaffected
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val stomper = game.findPermanent("Cavern Stomper")!!
                val activation = game.execute(
                    ActivateAbility(playerId = game.player1Id, sourceId = stomper, abilityId = activateAbilityId)
                )
                check(activation.error == null) { "ability activation failed: ${activation.error}" }
                if (game.getPendingDecision() is SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                check(game.declareAttackers(mapOf("Cavern Stomper" to 2)).error == null) {
                    "Cavern Stomper should be able to attack"
                }
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                return game.declareBlockers(mapOf(blockerName to listOf("Cavern Stomper")))
            }

            test("granted 'can't be blocked by power 2 or less' stops a power-2 blocker") {
                withClue("a power-2 creature must not be able to block Cavern Stomper after the {3}{G} grant") {
                    declareBlockOnStomper("Grizzly Bears").error shouldNotBe null
                }
            }

            test("granted 'can't be blocked by power 2 or less' still lets a power-3 blocker through") {
                withClue("a power-3 creature is unaffected by the restriction and may block") {
                    declareBlockOnStomper("Hill Giant").error shouldBe null
                }
            }
        }
    }
}
