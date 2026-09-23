package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Guiding Hydra (Reality Fracture #11) — {X}{W} Creature — Hydra Horror 1/0:
 *   This creature enters with X +1/+1 counters on it.
 *   At the beginning of combat on your turn, you may remove a +1/+1 counter from this creature.
 *   If you do, put a +1/+1 counter on each other creature you control.
 *
 * Pins the X counters, the "you may" (declining changes nothing), and that the spread reaches each
 * *other* creature you control but not the Hydra itself or an opponent's creature.
 */
class GuidingHydraScenarioTest : ScenarioTestBase() {

    private fun TestGame.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    private fun castHydraWithTwo(): TestGame {
        val game = scenario().withPlayers()
            .withCardInHand(1, "Guiding Hydra")
            .withLandsOnBattlefield(1, "Plains", 3)
            .withCardOnBattlefield(1, "Grizzly Bears")
            .withCardOnBattlefield(2, "Centaur Courser")
            .withCardInLibrary(1, "Plains")
            .withCardInLibrary(2, "Plains")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

        game.castXSpell(1, "Guiding Hydra", 2).error shouldBe null
        game.resolveStack()
        return game
    }

    init {
        test("enters with X +1/+1 counters") {
            val game = castHydraWithTwo()
            val hydra = game.findPermanent("Guiding Hydra")!!
            game.plusOneCounters(hydra) shouldBe 2
            game.state.projectedState.getPower(hydra) shouldBe 3
            game.state.projectedState.getToughness(hydra) shouldBe 2
        }

        test("removing a counter at beginning of combat puts one on each other creature you control") {
            val game = castHydraWithTwo()
            val hydra = game.findPermanent("Guiding Hydra")!!
            val bears = game.findPermanent("Grizzly Bears")!!
            val courser = game.findPermanent("Centaur Courser")!!

            game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
            game.resolveStack()
            withClue("the trigger asks whether to remove a counter") {
                game.hasPendingDecision() shouldBe true
            }
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()

            game.plusOneCounters(hydra) shouldBe 1
            game.plusOneCounters(bears) shouldBe 1
            withClue("an opponent's creature gets nothing") {
                game.plusOneCounters(courser) shouldBe 0
            }
        }

        test("declining leaves every counter where it was") {
            val game = castHydraWithTwo()
            val hydra = game.findPermanent("Guiding Hydra")!!
            val bears = game.findPermanent("Grizzly Bears")!!

            game.passUntilPhase(Phase.COMBAT, Step.BEGIN_COMBAT)
            game.resolveStack()
            game.hasPendingDecision() shouldBe true
            game.answerYesNo(false).error shouldBe null
            game.resolveStack()

            game.plusOneCounters(hydra) shouldBe 2
            game.plusOneCounters(bears) shouldBe 0
        }
    }
}
