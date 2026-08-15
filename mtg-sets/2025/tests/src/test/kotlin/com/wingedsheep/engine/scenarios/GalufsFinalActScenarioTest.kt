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
 * Galuf's Final Act (FIN #186) — {1}{G} Instant.
 *
 *   "Until end of turn, target creature gets +1/+0 and gains 'When this creature dies, put a
 *    number of +1/+1 counters equal to its power on up to one target creature.'"
 *
 * Three things to prove end-to-end:
 *   1. The +1/+0 pump lands (projected power rises by 1) until end of turn.
 *   2. When the pumped creature dies, the granted trigger puts +1/+1 counters equal to its
 *      LAST-KNOWN power (the pumped value at death) on the chosen creature.
 *   3. "Up to one target creature" is optional — declining the target resolves with no counters.
 */
class GalufsFinalActScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        context("Galuf's Final Act") {

            test("pumps +1/+0, and on death grants +1/+1 counters equal to last-known power") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Galuf's Final Act")
                    // {1}{G} for the spell, plus {R} for Lightning Bolt to kill the creature.
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardInHand(1, "Lightning Bolt")
                    // The creature that will be pumped and then die.
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    // A second creature to receive the counters.
                    .withCardOnBattlefield(1, "Trained Armodon")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val recipient = game.findPermanent("Trained Armodon")!!
                val powerBefore = projector.getProjectedPower(game.state, bears)

                // Cast Galuf's Final Act on Grizzly Bears (2/2 → 3/2 until end of turn).
                game.castSpell(1, "Galuf's Final Act", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("the +1/+0 pump raises projected power by 1 (2 -> 3)") {
                    projector.getProjectedPower(game.state, bears) shouldBe powerBefore + 1
                }
                withClue("toughness is unchanged by +1/+0") {
                    projector.getProjectedToughness(game.state, bears) shouldBe 2
                }

                // Lightning Bolt (3 damage) kills the 3/2 Grizzly Bears.
                game.castSpell(1, "Lightning Bolt", targetId = bears).error shouldBe null
                game.resolveStack()

                // The granted dies trigger is now on the stack and asks for "up to one target
                // creature" to receive the counters. Choose the Trained Armodon.
                withClue("the dies trigger prompts for its up-to-one target") {
                    game.hasPendingDecision() shouldBe true
                }
                game.selectTargets(listOf(recipient)).error shouldBe null
                game.resolveStack()

                withClue("recipient gets +1/+1 counters equal to last-known power (3)") {
                    val counters = game.state.getEntity(recipient)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
                    counters shouldBe 3
                }
            }

            test("up to one target may be declined — no counters are placed") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Galuf's Final Act")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardInHand(1, "Lightning Bolt")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardOnBattlefield(1, "Trained Armodon")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val recipient = game.findPermanent("Trained Armodon")!!

                game.castSpell(1, "Galuf's Final Act", targetId = bears).error shouldBe null
                game.resolveStack()

                game.castSpell(1, "Lightning Bolt", targetId = bears).error shouldBe null
                game.resolveStack()

                withClue("the dies trigger prompts for its up-to-one target") {
                    game.hasPendingDecision() shouldBe true
                }
                // Decline the optional target.
                game.skipTargets().error shouldBe null
                game.resolveStack()

                withClue("declining places no +1/+1 counters anywhere") {
                    val counters = game.state.getEntity(recipient)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
                    counters shouldBe 0
                }
            }
        }
    }
}
