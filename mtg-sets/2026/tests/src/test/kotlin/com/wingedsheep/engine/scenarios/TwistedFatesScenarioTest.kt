package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

class TwistedFatesScenarioTest : ScenarioTestBase() {
    init {
        test("destroys the target permanent and puts a counter on each creature the target player controls") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Twisted Fates")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(1, "Hill Giant")
                .withCardOnBattlefield(2, "Sol Ring")
                .withCardOnBattlefield(2, "Goblin Piker")
                .withLandsOnBattlefield(1, "Plains", 3)
                .withLandsOnBattlefield(1, "Swamp", 2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val bears = game.findPermanent("Grizzly Bears")!!
            val giant = game.findPermanent("Hill Giant")!!
            val piker = game.findPermanent("Goblin Piker")!!
            game.execute(
                CastSpell(
                    game.player1Id,
                    game.findCardsInHand(1, "Twisted Fates").single(),
                    listOf(ChosenTarget.Permanent(game.findPermanent("Sol Ring")!!), ChosenTarget.Player(game.player1Id))
                )
            ).error shouldBe null
            game.resolveStack()
            game.isInGraveyard(2, "Sol Ring") shouldBe true
            fun counters(id: com.wingedsheep.sdk.model.EntityId) =
                game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
            counters(bears) shouldBe 1
            counters(giant) shouldBe 1
            counters(piker) shouldBe 0
        }
    }
}
