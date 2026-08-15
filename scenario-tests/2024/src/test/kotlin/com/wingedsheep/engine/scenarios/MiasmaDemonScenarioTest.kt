package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Miasma Demon (DSK #109) — {4}{B}{B} Demon, 5/4, Flying.
 *
 * "When this creature enters, you may discard any number of cards. When you do, up to that many
 * target creatures each get -2/-2 until end of turn."
 *
 * Exercises the pipeline-count-linked reflexive target cap: the number of cards discarded
 * (`discarded_count`) bounds how many creatures the reflexive trigger may target.
 */
class MiasmaDemonScenarioTest : ScenarioTestBase() {

    init {
        context("Miasma Demon") {

            test("discarding two cards lets it shrink up to two creatures by -2/-2") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Miasma Demon")
                    .withCardInHand(1, "Grizzly Bears")
                    .withCardInHand(1, "Glory Seeker")
                    .withLandsOnBattlefield(1, "Swamp", 6)
                    .withCardOnBattlefield(2, "Grizzly Bears") // 2/2
                    .withCardOnBattlefield(2, "Glory Seeker")  // 2/2
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val targetBears = game.findPermanent("Grizzly Bears")!!
                val targetSeeker = game.findPermanent("Glory Seeker")!!

                val cast = game.castSpell(1, "Miasma Demon")
                withClue("Casting Miasma Demon should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                // ETB reflexive: discard the two non-Demon cards in hand.
                val toDiscard = game.findCardsInHand(1, "Grizzly Bears") +
                    game.findCardsInHand(1, "Glory Seeker")
                withClue("Two discardable cards remain in hand") { toDiscard.size shouldBe 2 }
                game.selectCards(toDiscard)

                // "Up to that many" = 2: target both opposing creatures.
                game.selectTargets(listOf(targetBears, targetSeeker))
                game.resolveStack()

                withClue("Grizzly Bears (2/2) gets -2/-2 and dies") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
                withClue("Glory Seeker (2/2) gets -2/-2 and dies") {
                    game.isOnBattlefield("Glory Seeker") shouldBe false
                }
            }

            test("discarding one card caps the reflexive trigger at one target") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Miasma Demon")
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Swamp", 6)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardOnBattlefield(2, "Glory Seeker")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val targetBears = game.findPermanent("Grizzly Bears")!!
                val targetSeeker = game.findPermanent("Glory Seeker")!!

                game.castSpell(1, "Miasma Demon")
                game.resolveStack()

                // Discard the one non-Demon card.
                game.selectCards(game.findCardsInHand(1, "Grizzly Bears"))

                // Trying to target two creatures must be rejected (cap = discarded count = 1).
                val tooMany = game.selectTargets(listOf(targetBears, targetSeeker))
                withClue("Selecting two targets after discarding one is illegal") {
                    (tooMany.error != null) shouldBe true
                }

                // One target is legal.
                game.selectTargets(listOf(targetBears))
                game.resolveStack()

                withClue("The single targeted 2/2 dies to -2/-2") {
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
                withClue("The untargeted creature is unharmed") {
                    game.isOnBattlefield("Glory Seeker") shouldBe true
                }
            }

            test("discarding zero cards fires no reflexive effect") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Miasma Demon")
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Swamp", 6)
                    .withCardOnBattlefield(2, "Glory Seeker")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Miasma Demon")
                game.resolveStack()

                // Decline to discard (select no cards).
                game.skipSelection()
                game.resolveStack()

                withClue("No discard means no -2/-2, so the opposing creature survives") {
                    game.isOnBattlefield("Glory Seeker") shouldBe true
                }
                withClue("Miasma Demon resolved onto the battlefield") {
                    game.isOnBattlefield("Miasma Demon") shouldBe true
                }
            }
        }
    }
}
