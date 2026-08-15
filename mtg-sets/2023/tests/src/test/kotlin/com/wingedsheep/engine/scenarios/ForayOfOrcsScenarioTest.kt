package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario test for Foray of Orcs (LTR #128) — {3}{R} Sorcery.
 *
 * "Amass Orcs 2. When you do, Foray of Orcs deals X damage to target creature an opponent
 *  controls, where X is the amassed Army's power."
 *
 * Per the 2023-06-16 ruling, the damage half is a reflexive ability whose target is chosen
 * when the trigger goes on the stack — not at cast time. Verifies that the amass resolves
 * first, the just-amassed Orc Army survives the reflexive damage step, and the X value
 * reads the projected power of the chosen Army (`EntityReference.AmassedArmy`).
 */
class ForayOfOrcsScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Foray of Orcs") {

            test("creates a 2/2 Orc Army, then the reflexive trigger asks for a target and deals 2 damage") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Foray of Orcs")
                    .withLandsOnBattlefield(1, "Mountain", 4)
                    .withCardOnBattlefield(2, "Grizzly Bears") // 2/2
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Foray of Orcs")
                withClue("Casting Foray of Orcs should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                // Amass resolves first; the reflexive trigger lands on the stack and pauses
                // for target selection per CR 603.2c.
                game.resolveStack()

                // A 0/0 Orc Army token is on the battlefield with 2 +1/+1 counters → 2/2.
                val army = game.findPermanent("Orc Army")
                withClue("Amass should have created an Orc Army token") {
                    army shouldBe (army ?: error("missing Orc Army"))
                }
                val counters = game.state.getEntity(army!!)?.get<CountersComponent>()
                withClue("Amass Orcs 2 puts two +1/+1 counters on the Army") {
                    counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 2
                }

                // Reflexive trigger asks for "creature an opponent controls" target.
                val decision = game.getPendingDecision()
                decision.shouldBeInstanceOf<ChooseTargetsDecision>()
                val bear = game.findPermanent("Grizzly Bears")!!
                game.selectTargets(listOf(bear))
                game.resolveStack()

                // X = amassed Army's power = 2 (0/0 token + two +1/+1 counters → 2/2).
                // 2 damage to a 2/2 Bears is exactly lethal — Bears dies.
                withClue("Reflexive damage should be lethal to Grizzly Bears (2 damage to 2 toughness)") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
                // Army stays — the reflexive trigger doesn't touch it, and the amass already resolved.
                withClue("Orc Army survives — the reflexive trigger only targets the bear") {
                    game.isOnBattlefield("Orc Army") shouldBe true
                }
                withClue("Army's projected power is 2 (0 base + 2 counters)") {
                    projector.project(game.state).getPower(army) shouldBe 2
                }
            }
        }
    }
}
