package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Clockwork Avian (ATQ #45).
 *
 * {5} Artifact Creature — Bird 0/4, Flying
 * "This creature enters with four +1/+0 counters on it.
 *  At end of combat, if this creature attacked or blocked this combat, remove a +1/+0 counter from it."
 *
 * Exercises the new +1/+0 stat counter (layer 7c) and the attacked-or-blocked end-of-combat shed.
 */
class ClockworkAvianScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        fun plusOneZero(game: TestGame, id: com.wingedsheep.sdk.model.EntityId): Int =
            game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ZERO) ?: 0

        context("Clockwork Avian") {

            test("enters with four +1/+0 counters and is a 4/4") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Clockwork Avian")
                    .withLandsOnBattlefield(1, "Mountain", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Clockwork Avian").error shouldBe null
                game.resolveStack()

                val avian = game.findPermanent("Clockwork Avian")!!
                withClue("Enters with four +1/+0 counters") {
                    plusOneZero(game, avian) shouldBe 4
                }

                val projected = stateProjector.project(game.state)
                withClue("Base 0/4 + four +1/+0 counters projects as a 4/4 (power modified, toughness unchanged)") {
                    projected.getPower(avian) shouldBe 4
                    projected.getToughness(avian) shouldBe 4
                }
            }

            test("removes a +1/+0 counter at end of combat after attacking, becoming a 3/4") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Clockwork Avian")
                    .withActivePlayer(1)
                    .inPhase(Phase.BEGINNING, Step.UNTAP)
                    .build()

                // Stamp the four +1/+0 counters it would have entered with.
                val avian = game.findPermanent("Clockwork Avian")!!
                game.state = game.state.updateEntity(avian) { c ->
                    c.with(CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ZERO to 4)))
                }

                withClue("Starts at 4/4 with four counters") {
                    plusOneZero(game, avian) shouldBe 4
                    stateProjector.project(game.state).getPower(avian) shouldBe 4
                }

                // Advance into combat and attack with the Avian.
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Clockwork Avian" to 2)).error shouldBe null

                // Advance to end of combat; the end-of-combat trigger fires and resolves.
                game.passUntilPhase(Phase.COMBAT, Step.END_COMBAT)
                game.resolveStack()

                withClue("One +1/+0 counter shed at end of combat (attacked this combat)") {
                    plusOneZero(game, avian) shouldBe 3
                }
                withClue("With three +1/+0 counters it is a 3/4") {
                    val projected = stateProjector.project(game.state)
                    projected.getPower(avian) shouldBe 3
                    projected.getToughness(avian) shouldBe 4
                }
            }

            test("removes a +1/+0 counter at end of combat after blocking, becoming a 3/4") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Clockwork Avian")
                    .withCardOnBattlefield(2, "Grizzly Bears") // opponent's attacker
                    .withActivePlayer(2)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                // Stamp the four +1/+0 counters it would have entered with.
                val avian = game.findPermanent("Clockwork Avian")!!
                game.state = game.state.updateEntity(avian) { c ->
                    c.with(CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ZERO to 4)))
                }

                // The opponent attacks; player 1's Avian blocks the Bears (it never attacked itself —
                // this exercises the "or blocked this combat" half of the shed trigger).
                game.declareAttackers(mapOf("Grizzly Bears" to 1)).error shouldBe null
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareBlockers(mapOf("Clockwork Avian" to listOf("Grizzly Bears")))

                // Advance to end of combat; the end-of-combat trigger fires and resolves.
                game.passUntilPhase(Phase.COMBAT, Step.END_COMBAT)
                game.resolveStack()

                withClue("One +1/+0 counter shed at end of combat (blocked this combat)") {
                    plusOneZero(game, avian) shouldBe 3
                }
                withClue("With three +1/+0 counters it is a 3/4") {
                    val projected = stateProjector.project(game.state)
                    projected.getPower(avian) shouldBe 3
                    projected.getToughness(avian) shouldBe 4
                }
            }

            test("does not remove a counter at end of combat if it neither attacked nor blocked") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Clockwork Avian")
                    .withActivePlayer(1)
                    .inPhase(Phase.BEGINNING, Step.UNTAP)
                    .build()

                val avian = game.findPermanent("Clockwork Avian")!!
                game.state = game.state.updateEntity(avian) { c ->
                    c.with(CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ZERO to 4)))
                }

                // Pass through combat without declaring it as an attacker.
                game.passUntilPhase(Phase.COMBAT, Step.END_COMBAT)
                game.resolveStack()

                withClue("No combat participation → no counter removed (intervening-if fails)") {
                    plusOneZero(game, avian) shouldBe 4
                }
            }
        }
    }
}
