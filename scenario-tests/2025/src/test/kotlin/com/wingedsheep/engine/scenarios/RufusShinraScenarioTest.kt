package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Rufus Shinra (FIN #238).
 *
 * Rufus Shinra — {1}{W}{B} Legendary Creature — Human Noble 2/4.
 *   "Whenever Rufus Shinra attacks, if you don't control a creature named Darkstar, create
 *    Darkstar, a legendary 2/2 white and black Dog creature token."
 */
class RufusShinraScenarioTest : ScenarioTestBase() {

    init {
        test("attacking creates a single Darkstar token when none is controlled") {
            val game = scenario()
                .withPlayers()
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .withCardOnBattlefield(1, "Rufus Shinra", summoningSickness = false)
                .build()

            game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Rufus Shinra" to 2)).error shouldBe null
            game.resolveStack()

            withClue("a Darkstar token was created") {
                game.isOnBattlefield("Darkstar") shouldBe true
            }
            withClue("exactly one Darkstar exists (the intervening-if guards against duplicates)") {
                game.findAllPermanents("Darkstar").size shouldBe 1
            }
        }
    }
}
