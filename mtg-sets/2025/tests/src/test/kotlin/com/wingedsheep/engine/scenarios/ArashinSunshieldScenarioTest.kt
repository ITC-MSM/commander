package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Arashin Sunshield ({3}{W}, 3/4 Human Warrior).
 *
 * "When this creature enters, exile up to two target cards from a single graveyard.
 *  {W}, {T}: Tap target creature."
 *
 * Exercises the new `TargetObject.sameOwner` cross-target constraint: the two chosen
 * cards must come from one graveyard ("a single graveyard").
 */
class ArashinSunshieldScenarioTest : ScenarioTestBase() {

    init {
        context("Arashin Sunshield") {

            test("ETB exiles two cards from a single graveyard") {
                val game = scenario()
                    .withPlayers()
                    .withCardInHand(1, "Arashin Sunshield")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withCardInGraveyard(2, "Glory Seeker")
                    .withCardInGraveyard(2, "Hill Giant")
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(2, "Plains")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Arashin Sunshield").error shouldBe null
                game.resolveStack() // creature enters → ETB trigger on stack, asks for targets

                val target1 = game.findCardsInGraveyard(2, "Glory Seeker").first()
                val target2 = game.findCardsInGraveyard(2, "Hill Giant").first()
                val result = game.selectTargets(listOf(target1, target2))
                withClue("Two cards from the same graveyard is legal: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                game.graveyardSize(2) shouldBe 0
                game.state.getExile(game.player2Id).size shouldBe 2
            }

            test("ETB cannot exile cards from two different graveyards") {
                val game = scenario()
                    .withPlayers()
                    .withCardInHand(1, "Arashin Sunshield")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withCardInGraveyard(1, "Glory Seeker")
                    .withCardInGraveyard(2, "Hill Giant")
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(2, "Plains")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Arashin Sunshield").error shouldBe null
                game.resolveStack()

                val mine = game.findCardsInGraveyard(1, "Glory Seeker").first()
                val theirs = game.findCardsInGraveyard(2, "Hill Giant").first()
                val result = game.selectTargets(listOf(mine, theirs))
                withClue("Targets from two graveyards must be rejected") {
                    result.error shouldNotBe null
                }
            }

            test("ETB may exile zero cards (up to two)") {
                val game = scenario()
                    .withPlayers()
                    .withCardInHand(1, "Arashin Sunshield")
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withCardInGraveyard(2, "Glory Seeker")
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(2, "Plains")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Arashin Sunshield").error shouldBe null
                game.resolveStack()

                // "up to two" — decline by selecting nothing.
                game.skipTargets().error shouldBe null
                game.resolveStack()

                game.graveyardSize(2) shouldBe 1
                game.isOnBattlefield("Arashin Sunshield") shouldBe true
            }
        }
    }
}
