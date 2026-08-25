package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * First Day of Class (STX) — "Whenever a creature you control enters this turn, put a +1/+1
 * counter on it and it gains haste until end of turn. Learn."
 *
 * An instant that installs a turn-bounded, *filter-scoped* delayed triggered ability. Two
 * properties distinguish it from the one-shot shape that `fireOnce = true` would give:
 *
 * - it fires for **every** creature you control that enters this turn, not just the first;
 * - it is prospective — a creature already on the battlefield when it resolves never "enters"
 *   again, so it picks up nothing.
 */
class FirstDayOfClassScenarioTest : ScenarioTestBase() {

    private fun plusOneCounters(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()
            ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    init {
        context("the delayed trigger") {
            test("fires for every creature you control that enters this turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "First Day of Class")
                    .withCardInHand(1, "Grizzly Bears")
                    .withCardInHand(1, "Centaur Courser")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withLandsOnBattlefield(1, "Forest", 5)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "First Day of Class").error shouldBe null
                game.resolveStack()
                while (game.hasPendingDecision()) game.skipSelection()

                game.castSpell(1, "Grizzly Bears").error shouldBe null
                game.resolveStack()
                while (game.hasPendingDecision()) game.skipSelection()

                val bears = game.findPermanent("Grizzly Bears")
                bears.shouldNotBeNull()
                withClue("the first creature got a +1/+1 counter and haste") {
                    plusOneCounters(game, bears) shouldBe 1
                    game.state.projectedState.hasKeyword(bears, Keyword.HASTE) shouldBe true
                }

                game.castSpell(1, "Centaur Courser").error shouldBe null
                game.resolveStack()
                while (game.hasPendingDecision()) game.skipSelection()

                val courser = game.findPermanent("Centaur Courser")
                courser.shouldNotBeNull()
                withClue("the second one too — the trigger is not a one-shot") {
                    plusOneCounters(game, courser) shouldBe 1
                    game.state.projectedState.hasKeyword(courser, Keyword.HASTE) shouldBe true
                }
            }

            test("leaves creatures that were already on the battlefield alone") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "First Day of Class")
                    .withCardOnBattlefield(1, "Hill Giant")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val giant = game.findPermanent("Hill Giant")
                giant.shouldNotBeNull()

                game.castSpell(1, "First Day of Class").error shouldBe null
                game.resolveStack()
                while (game.hasPendingDecision()) game.skipSelection()

                withClue("it never entered after the trigger was installed, so no counter") {
                    plusOneCounters(game, giant) shouldBe 0
                }
                withClue("and no haste either") {
                    game.state.projectedState.hasKeyword(giant, Keyword.HASTE) shouldBe false
                }
            }
        }
    }
}
