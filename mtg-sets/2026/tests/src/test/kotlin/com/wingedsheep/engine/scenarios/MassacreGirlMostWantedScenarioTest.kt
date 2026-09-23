package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Massacre Girl, Most Wanted (FRA #234) — a death of another creature you control pings the
 * target opponent and gains you 1 life; any noncombat damage to an opponent grows her.
 */
class MassacreGirlMostWantedScenarioTest : ScenarioTestBase() {

    init {
        fun builder(): ScenarioBuilder = scenario()
            .withPlayers("Player", "Opponent")
            .withCardOnBattlefield(1, "Massacre Girl, Most Wanted")
            .withCardOnBattlefield(1, "Grizzly Bears")
            .withCardInHand(1, "Shock")
            .withLandsOnBattlefield(1, "Mountain", 1)
            .withCardInLibrary(1, "Mountain")
            .withCardInLibrary(2, "Mountain")
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

        fun TestGame.resolveAll() {
            var guard = 0
            while ((state.stack.isNotEmpty() || state.pendingDecision != null) && guard++ < 20) {
                if (state.pendingDecision != null) selectTargets(listOf(player2Id)).error shouldBe null
                else resolveStack()
            }
        }

        context("Massacre Girl, Most Wanted") {
            test("another creature dying pings the opponent, which in turn grows her") {
                val game = builder().build()
                val girl = game.findPermanent("Massacre Girl, Most Wanted").shouldNotBeNull()
                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()

                game.castSpell(1, "Shock", bears).error shouldBe null
                game.resolveAll()

                game.isOnBattlefield("Grizzly Bears") shouldBe false
                game.getLifeTotal(2) shouldBe 19
                game.getLifeTotal(1) shouldBe 21
                withClue("her own 1 damage is noncombat damage to an opponent") {
                    game.state.projectedState.getPower(girl) shouldBe 5
                }
            }

            test("noncombat damage from any source to an opponent adds a counter") {
                val game = builder().build()
                val girl = game.findPermanent("Massacre Girl, Most Wanted").shouldNotBeNull()

                game.castSpellTargetingPlayer(1, "Shock", 2).error shouldBe null
                game.resolveAll()

                game.getLifeTotal(2) shouldBe 18
                game.state.projectedState.getPower(girl) shouldBe 5
                game.state.projectedState.getToughness(girl) shouldBe 5
            }
        }
    }
}
