package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class AshesToAshesScenarioTest : ScenarioTestBase() {
    init {
        test("exiles two nonartifact creatures and deals 5 damage to its controller") {
            val game = scenario()
                .withPlayers("Player", "Opponent")
                .withCardInHand(1, "Ashes to Ashes")
                .withLandsOnBattlefield(1, "Swamp", 3)
                .withCardOnBattlefield(2, "Grizzly Bears")
                .withCardOnBattlefield(2, "Hill Giant")
                .withLifeTotal(1, 20)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val spell = game.findCardsInHand(1, "Ashes to Ashes").single()
            val bears = game.findPermanent("Grizzly Bears")!!
            val giant = game.findPermanent("Hill Giant")!!
            val result = game.execute(
                CastSpell(
                    game.player1Id,
                    spell,
                    listOf(
                        entityIdToChosenTarget(game.state, bears),
                        entityIdToChosenTarget(game.state, giant),
                    ),
                )
            )

            result.error shouldBe null
            game.resolveStack()
            game.isInExile(2, "Grizzly Bears") shouldBe true
            game.isInExile(2, "Hill Giant") shouldBe true
            game.getLifeTotal(1) shouldBe 15
        }
    }
}
