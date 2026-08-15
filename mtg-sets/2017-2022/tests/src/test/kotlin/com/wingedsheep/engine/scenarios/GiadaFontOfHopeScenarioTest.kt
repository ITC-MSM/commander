package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.matchers.shouldBe

/**
 * Giada, Font of Hope — "Each other Angel you control enters with an additional +1/+1 counter on
 * it for each Angel you already control."
 *
 * The Scryfall ruling is the interesting part: the count is every Angel you control *other than*
 * the one entering, and it *includes* Giada. So Giada alone gives the next Angel one counter, and
 * Giada plus one other Angel gives the next Angel two.
 */
class GiadaFontOfHopeScenarioTest : ScenarioTestBase() {

    private fun TestGame.plusOneCounters(id: EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    init {
        test("another Angel enters with one +1/+1 counter when Giada is the only Angel") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Giada, Font of Hope")
                .withCardInHand(1, "Serra Angel")
                .withLandsOnBattlefield(1, "Plains", 5)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.castSpell(1, "Serra Angel")
            game.resolveStack()

            val serra = game.findPermanent("Serra Angel")!!
            // Giada herself is the one Angel "already controlled".
            game.plusOneCounters(serra) shouldBe 1
        }

        test("the count includes every other Angel, not just Giada") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Giada, Font of Hope")
                .withCardOnBattlefield(1, "Serra Angel")
                .withCardInHand(1, "Angel of Mercy")
                .withLandsOnBattlefield(1, "Plains", 6)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.castSpell(1, "Angel of Mercy")
            game.resolveStack()

            val mercy = game.findPermanent("Angel of Mercy")!!
            // Giada + Serra Angel = two Angels already controlled; the entering Angel isn't counted.
            game.plusOneCounters(mercy) shouldBe 2
        }

        test("Giada does not give herself counters, and non-Angels get none") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Giada, Font of Hope")
                .withCardInHand(1, "Grizzly Bears")
                .withLandsOnBattlefield(1, "Forest", 3)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.plusOneCounters(game.findPermanent("Giada, Font of Hope")!!) shouldBe 0

            game.castSpell(1, "Grizzly Bears")
            game.resolveStack()

            game.plusOneCounters(game.findPermanent("Grizzly Bears")!!) shouldBe 0
        }

        test("an opponent's Angel is unaffected") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Giada, Font of Hope")
                .withCardInHand(2, "Serra Angel")
                .withLandsOnBattlefield(2, "Plains", 5)
                .withActivePlayer(2)
                .withPriorityPlayer(2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.castSpell(2, "Serra Angel")
            game.resolveStack()

            game.plusOneCounters(game.findPermanent("Serra Angel")!!) shouldBe 0
        }
    }
}
