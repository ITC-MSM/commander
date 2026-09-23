package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.shouldBe

/**
 * Desperate Futurescribe (FRA #129) — at the beginning of combat on your turn, another target
 * creature you control gets +1/+1 until end of turn, or a +1/+1 counter instead if you've scried or
 * surveilled this turn.
 */
class DesperateFuturescribeScenarioTest : ScenarioTestBase() {
    init {
        fun game(withOpt: Boolean) = scenario().withPlayers()
            .withCardOnBattlefield(1, "Desperate Futurescribe")
            .withCardOnBattlefield(1, "Grizzly Bears")
            .apply { if (withOpt) withCardInHand(1, "Opt") }
            .withLandsOnBattlefield(1, "Island", 1)
            .withCardInLibrary(1, "Island")
            .withCardInLibrary(1, "Island")
            .withCardInLibrary(2, "Island")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
            .build()

        fun toCombatTrigger(game: TestGame) {
            game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
            val bears = game.findPermanent("Grizzly Bears")!!
            if (game.hasPendingDecision()) game.selectTargets(listOf(bears)).error shouldBe null
            game.resolveStack()
        }

        fun plusCounters(game: TestGame): Int {
            val bears = game.findPermanent("Grizzly Bears")!!
            return game.state.getEntity(bears)?.get<CountersComponent>()
                ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0
        }

        test("without a scry or surveil this turn it's +1/+1 until end of turn") {
            val game = game(withOpt = false)
            toCombatTrigger(game)
            val bears = game.findPermanent("Grizzly Bears")!!
            plusCounters(game) shouldBe 0
            game.state.projectedState.getPower(bears) shouldBe 3
            game.state.projectedState.getToughness(bears) shouldBe 3
        }

        test("after a scry this turn it's a +1/+1 counter instead") {
            val game = game(withOpt = true)
            game.castSpell(1, "Opt").error shouldBe null
            game.resolveStack()
            if (game.hasPendingDecision()) game.skipSelection()
            game.resolveStack()

            toCombatTrigger(game)
            val bears = game.findPermanent("Grizzly Bears")!!
            plusCounters(game) shouldBe 1
            game.state.projectedState.getPower(bears) shouldBe 3 // the counter only, not both
        }
    }
}
