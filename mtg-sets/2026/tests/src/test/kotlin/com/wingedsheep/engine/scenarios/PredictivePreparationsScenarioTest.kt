package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull

class PredictivePreparationsScenarioTest : ScenarioTestBase() {
    init {
        test("flashback adds a counter and exiles the spell") {
            val game = scenario().withPlayers().withCardInGraveyard(1, "Predictive Preparations")
                .withCardOnBattlefield(1, "Grizzly Bears").withLandsOnBattlefield(1, "Plains", 4)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
            val spell = game.findCardsInGraveyard(1, "Predictive Preparations").single()
            val bears = game.findPermanent("Grizzly Bears")!!
            val flashback = game.getLegalActions(1).map { it.action }.filterIsInstance<CastSpell>()
                .first { it.cardId == spell }
            game.execute(flashback.copy(targets = listOf(ChosenTarget.Permanent(bears)))).error shouldBe null
            game.resolveStack()
            game.state.getEntity(bears)!!.get<CountersComponent>()!!.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
            game.isInExile(1, "Predictive Preparations") shouldBe true
        }
        for (count in 0..2) {
            test("casting with $count targets") {
                val game = scenario().withPlayers().withCardInHand(1, "Predictive Preparations")
                    .withCardOnBattlefield(1, "Grizzly Bears").withCardOnBattlefield(2, "Hill Giant")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()
                val targets = listOf(game.findPermanent("Grizzly Bears")!!, game.findPermanent("Hill Giant")!!).take(count)
                val result = game.execute(CastSpell(game.player1Id, game.findCardsInHand(1, "Predictive Preparations").single(),
                    targets.map { ChosenTarget.Permanent(it) }))
                if (count == 0) {
                    result.error.shouldNotBeNull()
                } else {
                    result.error shouldBe null
                    game.resolveStack()
                    for (target in targets) {
                        game.state.getEntity(target)!!.get<CountersComponent>()!!.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 1
                    }
                }
            }
        }
    }
}
