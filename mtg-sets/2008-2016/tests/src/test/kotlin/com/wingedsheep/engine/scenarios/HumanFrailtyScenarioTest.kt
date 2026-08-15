package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

class HumanFrailtyScenarioTest : ScenarioTestBase() {
    init {
        test("destroys a target Human") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Human Frailty")
                .withLandsOnBattlefield(1, "Swamp", 1)
                .withCardOnBattlefield(2, "Cathedral Sanctifier")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val human = game.findPermanent("Cathedral Sanctifier")!!
            val cardId = game.state.getHand(game.player1Id).first {
                game.state.getEntity(it)?.get<CardComponent>()?.name == "Human Frailty"
            }
            val cast = game.execute(
                CastSpell(game.player1Id, cardId, listOf(ChosenTarget.Permanent(human))),
            )
            withClue("${cast.error}") { cast.error shouldBe null }
            game.resolveStack()
            game.findPermanent("Cathedral Sanctifier") shouldBe null
        }
    }
}
