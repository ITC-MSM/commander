package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class SkilledBattlecarverScenarioTest : ScenarioTestBase() {
    init {
        for (active in listOf(1, 2)) {
            test("first strike follows the active player $active") {
                val game = scenario().withPlayers().withCardOnBattlefield(1, "Skilled Battlecarver")
                    .withActivePlayer(active).inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
                game.state.projectedState.hasKeyword(game.findPermanent("Skilled Battlecarver")!!, Keyword.FIRST_STRIKE) shouldBe (active == 1)
            }
        }
        test("activated ability increases power only") {
            val game = scenario().withPlayers().withCardOnBattlefield(1, "Skilled Battlecarver")
                .withLandsOnBattlefield(1, "Mountain", 2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val creature = game.findPermanent("Skilled Battlecarver")!!
            val ability = cardRegistry.getCard("Skilled Battlecarver")!!.script.activatedAbilities.single().id
            game.execute(ActivateAbility(game.player1Id, creature, ability)).error shouldBe null
            game.resolveStack()
            game.state.projectedState.getPower(creature) shouldBe 3
            game.state.projectedState.getToughness(creature) shouldBe 1
        }
    }
}
