package com.wingedsheep.gym

import com.wingedsheep.engine.core.DeclareBlockers
import com.wingedsheep.engine.core.GameConfig
import com.wingedsheep.engine.core.PlayerConfig
import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.gym.contract.ActionParameterizer
import com.wingedsheep.gym.contract.ActionParams
import com.wingedsheep.gym.contract.LegalActionView
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.gym.contract.TrainingObservation
import com.wingedsheep.mtg.sets.definitions.por.PortalSet
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

/**
 * Combat and spell-targeting over the gym's action contract.
 *
 * The enumerator offers exactly one `DeclareAttackers` action, carrying an **empty** attacker map —
 * the choice of who attacks (and whom) rides in `validAttackers` / `validAttackTargets`, not in the
 * action. Stepping it by ID alone therefore declares no attackers: a legal move, an accepted step,
 * and a permanently attack-free game. That is what made combat unreachable through the gym API
 * while every request looked successful, so these tests are the guard on `ActionParams` closing it.
 *
 * The same template shape covers blocks, a spell's targets, and X — one test per `ActionParams`
 * field, so no branch of [ActionParameterizer] is unexercised.
 */
class GameGymEnvCombatTest : FunSpec({

    fun registry(): CardRegistry = CardRegistry().apply {
        register(PortalSet.cards)
        register(PortalSet.basicLands)
    }

    /** Mountains and hasty one-drops, so an attack is reachable within a few turns. */
    fun gobboDeck() = Deck.of("Mountain" to 17, "Raging Goblin" to 3)

    /** Mountains and Blaze — `{X}{R}`, "deals X damage to any target" — for the targets/X path. */
    fun blazeDeck() = Deck.of("Mountain" to 16, "Blaze" to 4)

    /**
     * Fixed so the shuffle is reproducible: these tests drive a real game until a particular action
     * shows up, and an unseeded run could bury every relevant card and deck a player out first.
     * `GameConfig.seed` exists for exactly this.
     */
    val SEED = 20260817L

    /** The env plus the environment behind it, for tests that need the raw [GameEnvironment.state]. */
    fun newEnv(deck: Deck = gobboDeck()): Pair<GameGymEnv, GameEnvironment> {
        val environment = GameEnvironment.create(registry())
        environment.reset(
            GameConfig(
                players = listOf(
                    PlayerConfig("Alice", deck),
                    PlayerConfig("Bob", deck)
                ),
                skipMulligans = true,
                startingPlayerIndex = 0,
                seed = SEED
            )
        )
        // revealAll: we drive both seats, so neither hand may be masked.
        return GameGymEnv(environment, perspectivePlayerIndex = 0, defaultRevealAll = true) to environment
    }

    fun TrainingObservation.actionOf(kind: String): LegalActionView? =
        legalActions.firstOrNull { it.kind == kind }

    fun TrainingObservation.lowestLife(): Int = players.minOf { it.lifeTotal }

    /**
     * Advance with cheap, purposeless moves — land, affordable spell, pass — until [wanted] finds an
     * action worth stopping on, and return it. Null if the game ended or ran out of budget first.
     */
    fun GameGymEnv.driveUntil(
        observation: TrainingObservation,
        budget: Int = 400,
        wanted: (TrainingObservation) -> LegalActionView?
    ): Pair<TrainingObservation, LegalActionView>? {
        var current = observation
        repeat(budget) {
            if (current.terminated) return null
            wanted(current)?.let { return current to it }
            // Candidates in preference order. There is no field that says "this cast is completable
            // from an ID alone" — `minTargets` / `maxTargets` are placeholders the enumerator leaves
            // set even on an untargeted spell (Raging Goblin reports 1/1) — so try, and fall through
            // when the engine refuses. That refusal being an exception rather than a silent no-op is
            // the whole point of ActionParams; a spell needing real targets is stepped by the tests
            // themselves, with params.
            val candidates = listOfNotNull(
                current.actionOf("PlayLand"),
                current.legalActions.firstOrNull { it.kind == "CastSpell" && it.affordable },
                current.actionOf("PassPriority"),
                current.legalActions.firstOrNull()
            )
            current = candidates.firstNotNullOfOrNull { candidate ->
                try {
                    step(candidate.actionId).observation as TrainingObservation
                } catch (_: IllegalArgumentException) {
                    null
                }
            } ?: return null
        }
        return null
    }

    test("attackers declared through step params deal combat damage") {
        val (env, _) = newEnv()
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
        val (env, _) = newEnv()
        val start = env.observe().observation as TrainingObservation

        val found = env.driveUntil(start) { obs ->
            obs.actionOf("DeclareAttackers")?.takeIf { it.validAttackers.isNotEmpty() }
        }
        withClue("never reached a declare-attackers step") { (found != null).shouldBeTrue() }
        val (observation, attack) = found!!

        // A player is never a legal attacker. The engine rejects the declaration, which leaves the
        // state untouched — indistinguishable from "attacked with nobody" unless it is surfaced.
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
    }

    test("params an action cannot use are rejected") {
        val (env, _) = newEnv()
        val observation = env.observe().observation as TrainingObservation
        val pass = observation.actionOf("PassPriority") ?: observation.legalActions.first()

        shouldThrow<IllegalArgumentException> {
            env.step(pass.actionId, ActionParams(xValue = 3))
        }
    }

    test("stepping without params still means 'attack with nobody'") {
        val (env, _) = newEnv()
        val start = env.observe().observation as TrainingObservation

        val found = env.driveUntil(start) { obs ->
            obs.actionOf("DeclareAttackers")?.takeIf { it.validAttackers.isNotEmpty() }
        }
        withClue("never reached a declare-attackers step") { (found != null).shouldBeTrue() }
        val (_, attack) = found!!

        val after = env.step(attack.actionId).observation as TrainingObservation
        withClue("the empty declaration is accepted and nobody is attacking") {
            after.actionOf("DeclareAttackers") shouldBe null
        }
    }

    context("targets and X — the CastSpell / ActivateAbility branch") {

        test("a targeted X spell resolves for the X and at the target the params name") {
            val (env, _) = newEnv(blazeDeck())
            val start = env.observe().observation as TrainingObservation

            // Blaze is {X}{R}; wait until enough Mountains are out to afford X >= 2.
            val found = env.driveUntil(start) { obs ->
                obs.legalActions.firstOrNull { la ->
                    la.kind == "CastSpell" && la.affordable && la.hasXCost &&
                        (la.maxAffordableX ?: 0) >= 2
                }
            }
            withClue("never reached an affordable Blaze with X >= 2") { (found != null).shouldBeTrue() }
            val (observation, cast) = found!!

            val caster = observation.players.first { it.id == observation.agentToAct }
            val victim = observation.players.first { it.id != caster.id }
            val lifeBefore = victim.lifeTotal

            val after = env.step(
                cast.actionId,
                ActionParams(targets = listOf(victim.id), xValue = 2)
            ).observation as TrainingObservation

            // Pass priority until the spell has resolved and the damage has landed.
            val settled = env.driveUntil(after) { obs ->
                obs.legalActions.firstOrNull()
                    ?.takeIf { obs.players.first { p -> p.id == victim.id }.lifeTotal != lifeBefore }
            }?.first ?: after

            withClue("X=2 damage was dealt to the player the params named, not to nobody") {
                settled.players.first { it.id == victim.id }.lifeTotal shouldBe lifeBefore - 2
            }
        }

        test("a bare entity id in targets resolves to the zone variant the state implies") {
            val (_, environment) = newEnv(blazeDeck())
            val state = environment.state

            // Which variant an id means is a fact about the state, not the request: a player id is a
            // player target, a card sitting in a hand or library is a card target carrying its owner
            // and zone, and an id in no zone at all is a caller mistake rather than a fallback.
            val playerId = state.turnOrder.first()
            ActionParameterizer.resolveTarget(playerId, state) shouldBe ChosenTarget.Player(playerId)

            val handCard = state.zones.entries
                .first { (key, ids) -> key.zoneType == Zone.HAND && ids.isNotEmpty() }
            val cardId = handCard.value.first()
            ActionParameterizer.resolveTarget(cardId, state) shouldBe ChosenTarget.Card(
                cardId = cardId,
                ownerId = handCard.key.ownerId,
                zone = Zone.HAND
            )

            shouldThrow<IllegalArgumentException> {
                ActionParameterizer.resolveTarget(EntityId("not-in-any-zone"), state)
            }
        }
    }

    context("blockers — the DeclareBlockers branch") {

        test("blockers params complete the template the enumerator could only offer empty") {
            val (_, environment) = newEnv()
            val blocker = EntityId("blocker")
            val attacker = EntityId("attacker")
            val template = DeclareBlockers(environment.state.turnOrder.first(), emptyMap())

            val completed = ActionParameterizer.apply(
                template,
                ActionParams(blockers = mapOf(blocker to listOf(attacker))),
                environment.state
            )

            withClue("the empty map is replaced by the caller's declaration") {
                (completed as DeclareBlockers).blockers shouldBe mapOf(blocker to listOf(attacker))
            }
        }

        test("a DeclareBlockers action rejects attacker / target / X params") {
            val (_, environment) = newEnv()
            val template = DeclareBlockers(environment.state.turnOrder.first(), emptyMap())
            val rejected = listOf(
                ActionParams(attackers = mapOf(EntityId("a") to EntityId("b"))),
                ActionParams(targets = listOf(EntityId("a"))),
                ActionParams(xValue = 1)
            )

            for (params in rejected) {
                withClue("$params must not be silently dropped") {
                    shouldThrow<IllegalArgumentException> {
                        ActionParameterizer.apply(template, params, environment.state)
                    }
                }
            }
        }
    }
})
