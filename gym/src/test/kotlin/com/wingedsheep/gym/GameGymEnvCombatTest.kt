package com.wingedsheep.gym

import com.wingedsheep.engine.core.GameConfig
import com.wingedsheep.engine.core.PlayerConfig
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.gym.contract.ActionParams
import com.wingedsheep.gym.contract.LegalActionView
import com.wingedsheep.gym.contract.TrainingObservation
import com.wingedsheep.mtg.sets.definitions.por.PortalSet
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

/**
 * Combat over the gym's action contract.
 *
 * The enumerator offers exactly one `DeclareAttackers` action, carrying an **empty** attacker map —
 * the choice of who attacks (and whom) rides in `validAttackers` / `validAttackTargets`, not in the
 * action. Stepping it by ID alone therefore declares no attackers: a legal move, an accepted step,
 * and a permanently attack-free game. That is what made combat unreachable through the gym API
 * while every request looked successful, so these tests are the guard on `ActionParams` closing it.
 */
class GameGymEnvCombatTest : FunSpec({

    fun registry(): CardRegistry = CardRegistry().apply {
        register(PortalSet.cards)
        register(PortalSet.basicLands)
    }

    /** Mountains and hasty one-drops, so an attack is reachable within a few turns. */
    fun gobboDeck() = Deck.of("Mountain" to 17, "Raging Goblin" to 3)

    fun newEnv(): GameGymEnv {
        val environment = GameEnvironment.create(registry())
        environment.reset(
            GameConfig(
                players = listOf(
                    PlayerConfig("Alice", gobboDeck()),
                    PlayerConfig("Bob", gobboDeck())
                ),
                skipMulligans = true,
                startingPlayerIndex = 0
            )
        )
        // revealAll: we drive both seats, so neither hand may be masked.
        return GameGymEnv(environment, perspectivePlayerIndex = 0, defaultRevealAll = true)
    }

    fun TrainingObservation.actionOf(kind: String): LegalActionView? =
        legalActions.firstOrNull { it.kind == kind }

    fun TrainingObservation.lowestLife(): Int = players.minOf { it.lifeTotal }

    test("attackers declared through step params deal combat damage") {
        val env = newEnv()
        var observation = env.observe().observation as TrainingObservation
        var attacksDeclared = 0

        repeat(400) {
            if (observation.terminated) return@repeat
            val attack = observation.actionOf("DeclareAttackers")
            val playLand = observation.actionOf("PlayLand")
            val cast = observation.legalActions.firstOrNull {
                it.kind == "CastSpell" && it.affordable
            }

            observation = when {
                attack != null && attack.validAttackers.isNotEmpty() &&
                    attack.validAttackTargets.isNotEmpty() -> {
                    // Everything that can attack, all at the first legal defender.
                    val defender = attack.validAttackTargets.first()
                    val params = ActionParams(
                        attackers = attack.validAttackers.associateWith { defender }
                    )
                    attacksDeclared++
                    env.step(attack.actionId, params).observation as TrainingObservation
                }

                playLand != null ->
                    env.step(playLand.actionId).observation as TrainingObservation

                cast != null ->
                    env.step(cast.actionId).observation as TrainingObservation

                else -> {
                    val pass = observation.actionOf("PassPriority")
                        ?: observation.legalActions.firstOrNull()
                        ?: return@repeat
                    env.step(pass.actionId).observation as TrainingObservation
                }
            }
        }

        withClue("the driver reached a declare-attackers step at least once") {
            (attacksDeclared > 0).shouldBeTrue()
        }
        withClue("a deck of Mountains and vanilla hasty goblins can only change life by attacking") {
            (observation.lowestLife() < 20).shouldBeTrue()
        }
    }

    test("an illegal attacker declaration is rejected, not silently dropped") {
        val env = newEnv()
        var observation = env.observe().observation as TrainingObservation

        repeat(400) {
            val attack = observation.actionOf("DeclareAttackers")
            if (attack != null && attack.validAttackers.isNotEmpty()) {
                // A player is never a legal attacker. The engine rejects the declaration, which
                // leaves the state untouched — indistinguishable from "attacked with nobody" unless
                // the rejection is surfaced.
                shouldThrow<IllegalArgumentException> {
                    env.step(
                        attack.actionId,
                        ActionParams(
                            attackers = mapOf(
                                observation.players.first().id to attack.validAttackTargets.first()
                            )
                        )
                    )
                }
                return@test
            }
            val next = observation.actionOf("PlayLand")
                ?: observation.legalActions.firstOrNull { it.kind == "CastSpell" && it.affordable }
                ?: observation.actionOf("PassPriority")
                ?: observation.legalActions.firstOrNull()
                ?: return@repeat
            observation = env.step(next.actionId).observation as TrainingObservation
        }

        throw AssertionError("never reached a declare-attackers step")
    }

    test("params an action cannot use are rejected") {
        val env = newEnv()
        val observation = env.observe().observation as TrainingObservation
        val pass = observation.actionOf("PassPriority") ?: observation.legalActions.first()

        shouldThrow<IllegalArgumentException> {
            env.step(pass.actionId, ActionParams(xValue = 3))
        }
    }

    test("stepping without params still means 'attack with nobody'") {
        val env = newEnv()
        var observation = env.observe().observation as TrainingObservation

        repeat(400) {
            val attack = observation.actionOf("DeclareAttackers")
            if (attack != null && attack.validAttackers.isNotEmpty()) {
                observation = env.step(attack.actionId).observation as TrainingObservation
                withClue("the empty declaration is accepted and nobody is attacking") {
                    observation.actionOf("DeclareAttackers") shouldBe null
                }
                return@test
            }
            val next = observation.actionOf("PlayLand")
                ?: observation.legalActions.firstOrNull { it.kind == "CastSpell" && it.affordable }
                ?: observation.actionOf("PassPriority")
                ?: observation.legalActions.firstOrNull()
                ?: return@repeat
            observation = env.step(next.actionId).observation as TrainingObservation
        }

        throw AssertionError("never reached a declare-attackers step")
    }
})
