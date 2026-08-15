package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Martyrs of Korlis (ATQ #6).
 *
 * {3}{W}{W} Creature — Human 1/6
 * "As long as this creature is untapped, all damage that would be dealt to you by artifacts is
 *  dealt to this creature instead."
 *
 * Proves the new condition gate on the static [com.wingedsheep.sdk.scripting.RedirectDamage]
 * replacement ([com.wingedsheep.sdk.dsl.Conditions.SourceIsUntapped]): the redirect applies only
 * while Martyrs is untapped, only for artifact damage sources, and sends the damage to Martyrs
 * itself (not to its controller).
 */
class MartyrsOfKorlisScenarioTest : ScenarioTestBase() {

    init {
        context("Martyrs of Korlis") {

            fun damageOn(game: TestGame, name: String): Int =
                game.state.getEntity(game.findPermanent(name)!!)?.get<DamageComponent>()?.amount ?: 0

            fun activateTriskelion(game: TestGame, controllerNumber: Int, targetPlayerNumber: Int) {
                val trisk = game.findPermanent("Triskelion")!!
                val ability = cardRegistry.getCard("Triskelion")!!.script.activatedAbilities[0]
                val targetPlayerId = if (targetPlayerNumber == 1) game.player1Id else game.player2Id
                val controllerId = if (controllerNumber == 1) game.player1Id else game.player2Id
                val result = game.execute(
                    ActivateAbility(
                        playerId = controllerId,
                        sourceId = trisk,
                        abilityId = ability.id,
                        targets = listOf(entityIdToChosenTarget(game.state, targetPlayerId))
                    )
                )
                withClue("Activating Triskelion should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()
            }

            test("untapped Martyrs: artifact damage that would hit its controller is redirected to Martyrs") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    // P2 casts Triskelion (artifact source) so it enters with its three +1/+1 counters.
                    .withCardInHand(2, "Triskelion")
                    .withLandsOnBattlefield(2, "Mountain", 6)
                    // P1 controls an untapped Martyrs of Korlis.
                    .withCardOnBattlefield(1, "Martyrs of Korlis", tapped = false)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(2, "Triskelion").error shouldBe null
                game.resolveStack()

                // Triskelion (P2's artifact) targets P1 — Martyrs's controller.
                activateTriskelion(game, controllerNumber = 2, targetPlayerNumber = 1)

                withClue("P1 took no damage — it was redirected") {
                    game.getLifeTotal(1) shouldBe 20
                }
                withClue("Martyrs received the 1 artifact damage instead") {
                    damageOn(game, "Martyrs of Korlis") shouldBe 1
                }
            }

            test("tapped Martyrs: the redirect is off — artifact damage hits its controller") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(2, "Triskelion")
                    .withLandsOnBattlefield(2, "Mountain", 6)
                    // Martyrs is TAPPED, so the SourceIsUntapped gate fails.
                    .withCardOnBattlefield(1, "Martyrs of Korlis", tapped = true)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(2, "Triskelion").error shouldBe null
                game.resolveStack()

                activateTriskelion(game, controllerNumber = 2, targetPlayerNumber = 1)

                withClue("With Martyrs tapped the redirect is suppressed: P1 took the damage") {
                    game.getLifeTotal(1) shouldBe 19
                }
                withClue("Tapped Martyrs received no damage") {
                    damageOn(game, "Martyrs of Korlis") shouldBe 0
                }
            }

            test("non-artifact source: damage to the controller is NOT redirected even while Martyrs is untapped") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    // Prodigal Sorcerer is a non-artifact pinger ("{T}: 1 damage to any target").
                    .withCardOnBattlefield(2, "Prodigal Sorcerer", summoningSickness = false)
                    .withCardOnBattlefield(1, "Martyrs of Korlis", tapped = false)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val sorcerer = game.findPermanent("Prodigal Sorcerer")!!
                val ability = cardRegistry.getCard("Prodigal Sorcerer")!!.script.activatedAbilities[0]
                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player2Id,
                        sourceId = sorcerer,
                        abilityId = ability.id,
                        targets = listOf(entityIdToChosenTarget(game.state, game.player1Id))
                    )
                )
                withClue("Activating Prodigal Sorcerer should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                withClue("Non-artifact damage is not redirected: P1 took the damage") {
                    game.getLifeTotal(1) shouldBe 19
                }
                withClue("Martyrs received no damage from the non-artifact source") {
                    damageOn(game, "Martyrs of Korlis") shouldBe 0
                }
            }
        }
    }
}
