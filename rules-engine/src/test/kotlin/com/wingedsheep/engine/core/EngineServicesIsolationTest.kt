package com.wingedsheep.engine.core

import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.matchers.shouldBe

/**
 * Two engines in one JVM — the game server beside a gym, or parallel test specs — must not share
 * the card data their zone moves read. The zone service used to take its registry from a global
 * the most recently constructed [EngineServices] overwrote, so building a second engine silently
 * swapped the first one's card definitions out from under it.
 */
class EngineServicesIsolationTest : ScenarioTestBase() {
    init {
        test("building a second engine leaves the first engine's zone moves on its own card data") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Ajani, Outland Chaperone")
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            val ajani = game.findCardsInHand(1, "Ajani, Outland Chaperone").single()

            // A second engine that knows no cards at all.
            EngineServices(CardRegistry())

            // The printed loyalty (CR 306.5b) is read off this engine's definition of the card.
            val entered = game.zones.moveToZone(game.state, ajani, Zone.BATTLEFIELD).state
            entered.getEntity(ajani)!!.get<CountersComponent>()!!.getCount(CounterType.LOYALTY) shouldBe 3
        }
    }
}
