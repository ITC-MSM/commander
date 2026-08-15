package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Reputable Merchant (TDM #217).
 *
 * "{2/W}{2/B}{2/G} Creature — Human Citizen 2/2.
 *  When this creature enters or dies, put a +1/+1 counter on target creature you control."
 *
 * Covers both halves of the "enters or dies" pair of triggered abilities:
 *  - On enter, the controller targets a creature they control and places a +1/+1 counter.
 *  - On death, the controller's death trigger fires and again places a +1/+1 counter on a
 *    targeted creature they control.
 */
class ReputableMerchantScenarioTest : ScenarioTestBase() {

    init {
        context("Reputable Merchant") {

            test("ETB puts a +1/+1 counter on a target creature you control") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Reputable Merchant")
                    .withLandsOnBattlefield(1, "Swamp", 6)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(3) { builder = builder.withCardInLibrary(1, "Forest") }
                val game = builder.build()

                val cast = game.castSpell(1, "Reputable Merchant")
                withClue("Casting Reputable Merchant should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack() // Merchant enters → ETB trigger on stack, asks for a target.

                val bears = game.findPermanent("Grizzly Bears")!!
                withClue("ETB trigger should be waiting for a target") {
                    game.hasPendingDecision() shouldBe true
                }
                game.selectTargets(listOf(bears))
                game.resolveStack()

                val counters = game.state.getEntity(bears)?.get<CountersComponent>()
                withClue("Grizzly Bears should have a +1/+1 counter from the ETB trigger") {
                    (counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0) shouldBe 1
                }
            }

            test("dies trigger puts a +1/+1 counter on a target creature you control") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Reputable Merchant", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInHand(2, "Bring Low")
                    .withLandsOnBattlefield(2, "Mountain", 4)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(3) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(3) { builder = builder.withCardInLibrary(2, "Mountain") }
                val game = builder.build()

                val merchant = game.findPermanent("Reputable Merchant")!!
                // Opponent burns the Merchant for 3 (it's a 2/2) → it dies.
                val cast = game.castSpell(2, "Bring Low", merchant)
                withClue("Casting Bring Low should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack() // Bring Low resolves, Merchant dies, dies trigger on stack.

                withClue("Reputable Merchant should be gone from the battlefield") {
                    game.isOnBattlefield("Reputable Merchant") shouldBe false
                }
                withClue("Dies trigger should be waiting for a target") {
                    game.hasPendingDecision() shouldBe true
                }
                val bears = game.findPermanent("Grizzly Bears")!!
                game.selectTargets(listOf(bears))
                game.resolveStack()

                val counters = game.state.getEntity(bears)?.get<CountersComponent>()
                withClue("Grizzly Bears should have a +1/+1 counter from the dies trigger") {
                    (counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0) shouldBe 1
                }
            }
        }
    }
}
