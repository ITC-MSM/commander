package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.AlternativeCostType
import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Recursive Recruitment (FRA #147) — two 2/2 Cadets; flashed back, each gets a +1/+1 counter for
 * every three cards in your graveyard, counted after the spell itself has left it.
 */
class RecursiveRecruitmentScenarioTest : ScenarioTestBase() {

    init {
        context("Recursive Recruitment") {
            test("cast from hand, it makes two plain 2/2 Cadets") {
                var b = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Recursive Recruitment")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(6) { b = b.withCardInGraveyard(1, "Grizzly Bears") }
                val game = b.build()

                game.castSpell(1, "Recursive Recruitment").error shouldBe null
                game.resolveStack()

                val cadets = game.findPermanents("Cadet")
                cadets.size shouldBe 2
                cadets.forEach { game.state.projectedState.getPower(it) shouldBe 2 }
            }

            test("flashed back, each Cadet gets a counter for every three cards in your graveyard") {
                var b = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInGraveyard(1, "Recursive Recruitment")
                    .withLandsOnBattlefield(1, "Island", 7)
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withCardInLibrary(1, "Island")
                    .withCardInLibrary(2, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                // Seven other cards: with the spell on the stack, 7 / 3 rounds down to 2.
                repeat(7) { b = b.withCardInGraveyard(1, "Grizzly Bears") }
                val game = b.build()
                val spell = game.findCardsInGraveyard(1, "Recursive Recruitment").single()

                val cast = game.execute(
                    CastSpell(
                        playerId = game.player1Id,
                        cardId = spell,
                        useAlternativeCost = true,
                        alternativeCostType = AlternativeCostType.FLASHBACK,
                    )
                )
                withClue("flashback: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                val cadets = game.findPermanents("Cadet")
                cadets.size shouldBe 2
                cadets.forEach {
                    game.state.projectedState.getPower(it) shouldBe 4
                    game.state.projectedState.getToughness(it) shouldBe 4
                }
                game.isInExile(1, "Recursive Recruitment") shouldBe true
            }
        }
    }
}
