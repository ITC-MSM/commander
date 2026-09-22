package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull

class TheoreticalNecromancerScenarioTest : ScenarioTestBase() {
    init {
        for (self in listOf(false, true)) {
            test("graveyard ability ${if (self) "rejects itself" else "returns another creature"}") {
                val game = scenario().withPlayers().withCardInGraveyard(1, "Theoretical Necromancer")
                    .withCardInGraveyard(1, "Grizzly Bears").withLandsOnBattlefield(1, "Swamp", 4)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
                val source = game.findCardsInGraveyard(1, "Theoretical Necromancer").single()
                val target = if (self) source else game.findCardsInGraveyard(1, "Grizzly Bears").single()
                val ability = cardRegistry.getCard("Theoretical Necromancer")!!.script.activatedAbilities.single().id
                val result = game.execute(ActivateAbility(game.player1Id, source, ability,
                    listOf(ChosenTarget.Card(target, game.player1Id, Zone.GRAVEYARD))))
                if (self) {
                    result.error.shouldNotBeNull()
                    game.isInGraveyard(1, "Theoretical Necromancer") shouldBe true
                } else {
                    result.error shouldBe null
                    game.resolveStack()
                    game.isInHand(1, "Grizzly Bears") shouldBe true
                    game.isInExile(1, "Theoretical Necromancer") shouldBe true
                }
            }
        }
    }
}
