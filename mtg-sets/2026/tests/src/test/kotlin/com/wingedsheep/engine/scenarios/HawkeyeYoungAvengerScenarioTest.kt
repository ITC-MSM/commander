package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.scripting.targets.TargetPlayer
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Hawkeye, Young Avenger (Marvel Super Heroes #131).
 *
 * {3}{R} · Legendary Creature — Human Archer Hero · 2/4
 *   Reach
 *   If a source you control would deal noncombat damage to an opponent or a permanent an opponent
 *   controls, instead it deals that much damage plus X, where X is Hawkeye's power.
 *
 * The card is a [com.wingedsheep.sdk.scripting.ModifyDamageAmount] replacement scoped by
 * [com.wingedsheep.sdk.scripting.events.DamageType.NonCombat]. That damage-type scoping was
 * *silently unenforced* by the engine: the `ModifyDamageAmount` loop in `DamageUtils` checked the
 * source and recipient filters but not the damage type, while the `DoubleDamage` loop above it did
 * — and combat damage reaches both through the same
 * `applyStaticDamageAmplification(isCombatDamage = true)`. Hawkeye is the only card in the corpus
 * pairing `ModifyDamageAmount` with a non-`Any` damage type, so the "combat damage is untouched"
 * cases here are the regression guard for that fix.
 */
class HawkeyeYoungAvengerScenarioTest : ScenarioTestBase() {

    /** A burn spell, so there is a source of *noncombat* damage under Player 1's control. */
    private val arrowShot = card("Test Arrow Shot") {
        manaCost = "{R}"
        typeLine = "Instant"
        oracleText = "Test Arrow Shot deals 3 damage to target player."
        spell {
            val victim = target("target player", TargetPlayer())
            effect = Effects.DealDamage(3, victim)
        }
    }

    /** A vanilla 2/2, to attack with alongside Hawkeye. */
    private val bear = card("Test Bear") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
    }

    init {
        cardRegistry.register(arrowShot)
        cardRegistry.register(bear)

        context("noncombat damage — amplified by Hawkeye's power") {

            test("a burn spell you control deals its damage plus Hawkeye's power to an opponent") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Hawkeye, Young Avenger")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardInHand(1, "Test Arrow Shot")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingPlayer(1, "Test Arrow Shot", 2).error shouldBe null
                game.resolveStack()

                withClue("3 damage + Hawkeye's power 2 = 5") {
                    game.getLifeTotal(2) shouldBe 15
                }
            }

            test("the same spell aimed at yourself is not amplified — 'to an opponent' is scoped") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Hawkeye, Young Avenger")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardInHand(1, "Test Arrow Shot")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingPlayer(1, "Test Arrow Shot", 1).error shouldBe null
                game.resolveStack()

                withClue("a source you control damaging *you* gets no bonus") {
                    game.getLifeTotal(1) shouldBe 17
                }
            }

            test("with no Hawkeye on the battlefield the spell deals its printed damage") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardInHand(1, "Test Arrow Shot")
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingPlayer(1, "Test Arrow Shot", 2).error shouldBe null
                game.resolveStack()

                game.getLifeTotal(2) shouldBe 17
            }
        }

        context("combat damage — untouched") {

            test("your creatures' combat damage is not amplified, Hawkeye's own included") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Hawkeye, Young Avenger", summoningSickness = false)
                    .withCardOnBattlefield(1, "Test Bear", summoningSickness = false)
                    .withActivePlayer(1)
                    .withPriorityPlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(
                    mapOf("Hawkeye, Young Avenger" to 2, "Test Bear" to 2)
                ).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.getPendingDecision() != null) {
                    game.submitDefaultCombatDamage()
                    game.resolveStack()
                }

                withClue("2 + 2 of combat damage; the noncombat-only bonus must not apply") {
                    game.getLifeTotal(2) shouldBe 16
                }
            }
        }
    }
}
