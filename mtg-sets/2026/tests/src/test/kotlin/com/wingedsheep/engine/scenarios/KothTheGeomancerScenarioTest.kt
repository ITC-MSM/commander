package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.state.components.player.ManaPoolComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class KothTheGeomancerScenarioTest : ScenarioTestBase() {
    init {
        test("a Mountain entering deals 1 to each opponent and adds {R}") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Koth, the Geomancer")
                .withCardInHand(1, "Mountain")
                .withCardInLibrary(1, "Mountain")
                .withCardInLibrary(2, "Mountain")
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val mountain = game.findCardsInHand(1, "Mountain").single()
            game.execute(PlayLand(game.player1Id, mountain)).error shouldBe null
            game.resolveStack()

            game.getLifeTotal(2) shouldBe 19
            game.state.getEntity(game.player1Id)!!.get<ManaPoolComponent>()!!.red shouldBe 1
        }

        test("a non-Mountain land deals the damage but adds no mana") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Koth, the Geomancer")
                .withCardInHand(1, "Forest")
                .withCardInLibrary(1, "Forest")
                .withCardInLibrary(2, "Forest")
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            val forest = game.findCardsInHand(1, "Forest").single()
            game.execute(PlayLand(game.player1Id, forest)).error shouldBe null
            game.resolveStack()

            game.getLifeTotal(2) shouldBe 19
            (game.state.getEntity(game.player1Id)?.get<ManaPoolComponent>()?.red ?: 0) shouldBe 0
        }
    }
}
