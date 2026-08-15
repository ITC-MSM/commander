package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Trade Route Envoy (TDM #163).
 *
 * "{3}{G} Creature — Dog Soldier 4/3.
 *  When this creature enters, draw a card if you control a creature with a counter on it.
 *  If you don't draw a card this way, put a +1/+1 counter on this creature."
 *
 * Exercises both branches of the ETB [com.wingedsheep.sdk.scripting.effects.ConditionalEffect]:
 *  - With a counter-bearing creature already in play, the controller draws a card and the Envoy
 *    gains no counter.
 *  - With no counter-bearing creature, the controller draws nothing and the Envoy gains a +1/+1
 *    counter instead.
 */
class TradeRouteEnvoyScenarioTest : ScenarioTestBase() {

    init {
        context("Trade Route Envoy ETB") {

            test("draws a card when you control a creature with a counter") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Trade Route Envoy")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(3) { builder = builder.withCardInLibrary(1, "Forest") }
                val game = builder.build()

                // Give the Grizzly Bears a +1/+1 counter so the draw branch is satisfied.
                val bearsId = game.findPermanent("Grizzly Bears")!!
                game.state = game.state.updateEntity(bearsId) { c ->
                    c.with(CountersComponent().withAdded(CounterType.PLUS_ONE_PLUS_ONE, 1))
                }

                val handBefore = game.handSize(1)

                val cast = game.castSpell(1, "Trade Route Envoy")
                withClue("Casting Trade Route Envoy should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("Trade Route Envoy should be on the battlefield") {
                    game.isOnBattlefield("Trade Route Envoy") shouldBe true
                }
                // Casting moved the Envoy out of hand (-1); drawing a card adds it back (+1) => net unchanged.
                withClue("Controller should have drawn a card from the ETB") {
                    game.handSize(1) shouldBe handBefore
                }
                val envoyId = game.findPermanent("Trade Route Envoy")!!
                val counters = game.state.getEntity(envoyId)?.get<CountersComponent>()
                withClue("Envoy should have NO +1/+1 counter when it drew a card") {
                    (counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0) shouldBe 0
                }
            }

            test("adds a +1/+1 counter to itself when no counter-bearing creature is controlled") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Trade Route Envoy")
                    .withLandsOnBattlefield(1, "Forest", 4)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(3) { builder = builder.withCardInLibrary(1, "Forest") }
                val game = builder.build()

                // Bears has no counter, so no controlled creature satisfies the draw condition.
                val handBefore = game.handSize(1)

                val cast = game.castSpell(1, "Trade Route Envoy")
                withClue("Casting Trade Route Envoy should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("Trade Route Envoy should be on the battlefield") {
                    game.isOnBattlefield("Trade Route Envoy") shouldBe true
                }
                // Casting removed the Envoy from hand (-1) and no draw occurred => hand shrinks by one.
                withClue("Controller should NOT have drawn a card") {
                    game.handSize(1) shouldBe handBefore - 1
                }
                val envoyId = game.findPermanent("Trade Route Envoy")!!
                val counters = game.state.getEntity(envoyId)?.get<CountersComponent>()
                withClue("Envoy should have a +1/+1 counter since it didn't draw") {
                    (counters?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0) shouldBe 1
                }
            }
        }
    }
}
