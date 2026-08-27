package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Olivia's Attendants (VOW #172) — {4}{R}{R} Creature — Vampire, 6/6.
 *
 * "Menace
 *  Whenever this creature deals damage, create that many Blood tokens.
 *  {2}{R}: This creature deals 1 damage to any target."
 *
 * The trigger is the *bare* damage event — any type, any recipient — and its payoff is the amount
 * that event reported. Those are the two things a lookalike gets wrong, so the tests separate them:
 * six combat damage to a player makes six Blood, and the creature's own {2}{R} ping makes exactly
 * one. A card that read the count off the Attendants' *power* would make six either way, and one
 * that used the ordinary combat-damage-to-a-player trigger would make none at all off the ping.
 */
class OliviasAttendantsScenarioTest : ScenarioTestBase() {

    init {
        context("Olivia's Attendants") {

            test("six combat damage to a player makes six Blood tokens") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Olivia's Attendants", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.findPermanents("Blood").size shouldBe 0

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Olivia's Attendants" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareNoBlockers().error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.END_COMBAT)
                game.resolveStack()

                withClue("6 unblocked power is 6 damage, so 6 Blood") {
                    game.getLifeTotal(2) shouldBe 14
                    game.findPermanents("Blood").size shouldBe 6
                }
            }

            test("the pinger's 1 damage makes exactly one Blood — the count is the event, not the power") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Olivia's Attendants", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val attendants = game.findPermanent("Olivia's Attendants")!!
                val abilityId = cardRegistry.getCard("Olivia's Attendants")!!
                    .script.activatedAbilities.single().id

                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = attendants,
                        abilityId = abilityId,
                        targets = listOf(entityIdToChosenTarget(game.state, game.player2Id)),
                    )
                ).error shouldBe null
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("1 damage is 1 Blood, not 6") {
                    game.getLifeTotal(2) shouldBe 19
                    game.findPermanents("Blood").size shouldBe 1
                }
            }

            test("damage to blockers triggers too — the event is not combat-damage-to-a-player") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Olivia's Attendants", summoningSickness = false)
                    // Menace needs two blockers, so the opponent fields two.
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Olivia's Attendants" to 2)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareBlockers(
                    mapOf(
                        "Grizzly Bears" to listOf("Olivia's Attendants"),
                        "Hill Giant" to listOf("Olivia's Attendants"),
                    )
                ).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.END_COMBAT)
                game.resolveStack()

                withClue("six damage divided among the blockers is still six damage dealt") {
                    game.getLifeTotal(2) shouldBe 20
                    game.findPermanents("Blood").size shouldBe 6
                }
            }
        }
    }
}
