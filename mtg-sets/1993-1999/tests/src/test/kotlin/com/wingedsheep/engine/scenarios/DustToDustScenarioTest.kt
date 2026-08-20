package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class DustToDustScenarioTest : ScenarioTestBase() {
    init {
        test("exiles exactly two target artifacts") {
            val game = scenario()
                .withPlayers("Player", "Opponent")
                .withCardInHand(1, "Dust to Dust")
                .withLandsOnBattlefield(1, "Plains", 3)
                .withCardOnBattlefield(2, "Ornithopter")
                .withCardOnBattlefield(2, "Millstone")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val spell = game.findCardsInHand(1, "Dust to Dust").single()
            val ornithopter = game.findPermanent("Ornithopter")!!
            val millstone = game.findPermanent("Millstone")!!
            val result = game.execute(
                CastSpell(
                    game.player1Id,
                    spell,
                    listOf(
                        entityIdToChosenTarget(game.state, ornithopter),
                        entityIdToChosenTarget(game.state, millstone),
                    ),
                )
            )

            result.error shouldBe null
            game.resolveStack()
            game.isInExile(2, "Ornithopter") shouldBe true
            game.isInExile(2, "Millstone") shouldBe true
        }
    }
}
