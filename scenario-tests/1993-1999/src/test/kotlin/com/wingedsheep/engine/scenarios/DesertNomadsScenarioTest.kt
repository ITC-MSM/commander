package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.DeclareBlockers
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Desert Nomads' two abilities:
 *  1. Desertwalk — can't be blocked when defending player controls a Desert.
 *     (CR 702.14 landwalk variant, keyed off [com.wingedsheep.sdk.core.Subtype.DESERT].)
 *  2. Damage prevention — prevent all damage that would be dealt to Desert Nomads by Deserts.
 *     Implemented as a continuous `PreventDamage` replacement (CR 615) with source filter
 *     `Land.withSubtype("Desert")` and recipient `RecipientFilter.Self`.
 */
class DesertNomadsScenarioTest : ScenarioTestBase() {

    init {
        context("Desertwalk evasion") {

            test("cannot be blocked when defending player controls a Desert") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Desert Nomads")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Desert")
                    .withActivePlayer(1)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)

                val attack = game.declareAttackers(mapOf("Desert Nomads" to 2))
                withClue("Declaring Desert Nomads as attacker should succeed: ${attack.error}") {
                    attack.error shouldBe null
                }

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

                val block = game.execute(
                    DeclareBlockers(
                        game.player2Id,
                        mapOf(game.findPermanent("Grizzly Bears")!! to listOf(game.findPermanent("Desert Nomads")!!))
                    )
                )
                withClue("Blocking should fail due to Desertwalk: error was ${block.error}") {
                    block.isSuccess shouldBe false
                    (block.error ?: "").lowercase().contains("desertwalk") shouldBe true
                }
            }

            test("CAN be blocked when defending player controls no Desert") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Desert Nomads")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Mountain")
                    .withActivePlayer(1)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Desert Nomads" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

                val block = game.declareBlockers(mapOf("Grizzly Bears" to listOf("Desert Nomads")))
                withClue("Blocking should succeed when defender controls no Desert: ${block.error}") {
                    block.error shouldBe null
                }
            }
        }

        context("Desert damage prevention") {

            test("Desert's ping is prevented when targeting Desert Nomads") {
                // Attacker = player 2 so player 1's Desert can ping the attacking Desert Nomads.
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(2, "Desert Nomads")
                    .withCardOnBattlefield(1, "Desert")
                    .withActivePlayer(2)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Desert Nomads" to 1)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.END_COMBAT)
                // Active player has priority first in END_COMBAT; pass to the Desert's owner.
                if (game.state.priorityPlayerId == game.player2Id) {
                    game.passPriority()
                }

                val nomadsId = game.findPermanent("Desert Nomads")!!
                val desertId = game.findPermanent("Desert")!!
                val pingAbility = cardRegistry.getCard("Desert")!!.script.activatedAbilities[1]

                val activation = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = desertId,
                        abilityId = pingAbility.id,
                        targets = listOf(entityIdToChosenTarget(game.state, nomadsId)),
                    )
                )
                withClue("Desert activation should succeed: ${activation.error}") {
                    activation.error shouldBe null
                }
                game.resolveStack()

                // Verify the prevention actually zeroed the damage: no damage marked.
                val damage = game.state.getEntity(nomadsId)
                    ?.get<com.wingedsheep.engine.state.components.battlefield.DamageComponent>()
                    ?.amount ?: 0
                withClue("Desert's ping should be fully prevented (damage marked = 0)") {
                    damage shouldBe 0
                }
                withClue("Desert Nomads should still be on the battlefield") {
                    game.isOnBattlefield("Desert Nomads") shouldBe true
                }
            }
        }
    }
}
