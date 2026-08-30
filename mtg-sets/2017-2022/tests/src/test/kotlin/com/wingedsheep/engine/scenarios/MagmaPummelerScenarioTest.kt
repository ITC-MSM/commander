package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.CombatResolutionDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario test for Magma Pummeler (VOW #169) — {X}{R}{R} Creature — Elemental, 0/0.
 *
 *   This creature enters with X +1/+1 counters on it.
 *   If damage would be dealt to this creature while it has a +1/+1 counter on it, prevent that
 *   damage and remove that many +1/+1 counters from it. When one or more counters are removed from
 *   this creature this way, it deals that much damage to any target.
 *
 * Covers the two axes the card adds to `PreventDamageByRemovingCounter` — `EqualToDamage` removal
 * and the `requiresCounter` gate — plus the "this way" scope on the counters-removed trigger.
 *
 * The case that pins the semantics is damage *above* the counter count: the printed ruling says all
 * of it is prevented and all the counters go, and the trigger then deals the number of counters
 * **removed**, not the damage. Getting that backwards is the obvious wrong implementation.
 */
class MagmaPummelerScenarioTest : ScenarioTestBase() {

    init {
        fun counters(game: TestGame, id: EntityId): Int =
            game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

        context("Magma Pummeler") {

            test("enters with X +1/+1 counters") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Magma Pummeler")
                    .withLandsOnBattlefield(1, "Mountain", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castXSpell(1, "Magma Pummeler", 4).error shouldBe null
                game.resolveStack()

                val pummeler = game.findPermanent("Magma Pummeler")!!
                withClue("X=4 means four +1/+1 counters, so a 4/4") {
                    counters(game, pummeler) shouldBe 4
                    game.state.projectedState.getPower(pummeler) shouldBe 4
                    game.state.projectedState.getToughness(pummeler) shouldBe 4
                }
            }

            test("damage below the counter count removes that many and pings for that much") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Magma Pummeler")
                    .withCardInHand(1, "Shock")
                    .withLandsOnBattlefield(1, "Mountain", 8)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castXSpell(1, "Magma Pummeler", 4).error shouldBe null
                game.resolveStack()
                val pummeler = game.findPermanent("Magma Pummeler")!!

                // Shock deals 2 to it: prevented, two counters removed.
                game.castSpell(1, "Shock", pummeler).error shouldBe null
                game.resolveStack()

                withClue("two counters removed, so a 2/2 survives") {
                    counters(game, pummeler) shouldBe 2
                    game.isOnBattlefield("Magma Pummeler") shouldBe true
                }

                game.selectTargets(listOf(game.player2Id))
                game.resolveStack()

                withClue("the trigger deals 2 — the counters removed") {
                    game.getLifeTotal(2) shouldBe 18
                }
            }

            test("damage above the counter count is all prevented and the ping is the counters removed") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Magma Pummeler")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castXSpell(1, "Magma Pummeler", 2).error shouldBe null
                game.resolveStack()
                val pummeler = game.findPermanent("Magma Pummeler")!!

                // Bolt deals 3 to a 2/2 Pummeler: all 3 prevented, both counters removed.
                game.castSpell(1, "Lightning Bolt", pummeler).error shouldBe null
                game.resolveStack()

                game.selectTargets(listOf(game.player2Id))
                game.resolveStack()

                withClue("the trigger deals 2 (counters removed), not 3 (the damage)") {
                    game.getLifeTotal(2) shouldBe 18
                }
                withClue("losing its last counters leaves a 0/0, which dies to the SBA") {
                    game.isOnBattlefield("Magma Pummeler") shouldBe false
                    game.isInGraveyard(1, "Magma Pummeler") shouldBe true
                }
            }

            test("combat damage from two blockers is one event, so it reads the total") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Magma Pummeler", summoningSickness = false)
                    .withCardOnBattlefield(2, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Centaur Courser", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                // Seeded in play rather than cast, because a Pummeler cast this turn can't attack.
                // This case is about the CR 510.2 batch, not the enters-with clause — that has its
                // own test above — so the counters go on directly.
                val pummeler = game.findPermanent("Magma Pummeler")!!
                game.state = game.state.updateEntity(pummeler) { c ->
                    c.with(
                        (c.get<CountersComponent>() ?: CountersComponent())
                            .withAdded(CounterType.PLUS_ONE_PLUS_ONE, 6)
                    )
                }

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Magma Pummeler" to 2)).error shouldBe null
                game.resolveStack()

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareBlockers(
                    mapOf(
                        "Grizzly Bears" to listOf("Magma Pummeler"),
                        "Centaur Courser" to listOf("Magma Pummeler")
                    )
                ).error shouldBe null

                // Passing priority is what makes the damage step's turn-based action actually run;
                // advanceToPhase alone moves the marker without dealing the damage. Stop at the
                // trigger's own target prompt rather than passUntilPhase, which cannot auto-answer
                // a ChooseTargetsDecision.
                repeat(12) {
                    val pending = game.getPendingDecision()
                    when {
                        pending is ChooseTargetsDecision -> return@repeat
                        // A double block makes the attacker's controller assign damage among the
                        // blockers first; take the engine's default split.
                        pending is CombatResolutionDecision -> game.submitDefaultCombatDamage()
                        else -> game.passPriority()
                    }
                }
                withClue("the prevention fired once and put its trigger on the stack") {
                    game.getPendingDecision().shouldBeInstanceOf<ChooseTargetsDecision>()
                }

                withClue("2 + 3 = 5 damage in one event (CR 510.2), so five counters go at once") {
                    counters(game, pummeler) shouldBe 1
                }
                withClue("all of it was prevented, so the Pummeler survives as a 1/1") {
                    game.isOnBattlefield("Magma Pummeler") shouldBe true
                }

                game.selectTargets(listOf(game.player2Id))
                game.resolveStack()
                withClue("the trigger deals 5 — the counters that one event removed") {
                    game.getLifeTotal(2) shouldBe 15
                }
            }
        }
    }
}
