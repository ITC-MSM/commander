package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Sarkhan's Resolve (TDM #158).
 *
 * "Choose one —
 *  • Target creature gets +3/+3 until end of turn.
 *  • Destroy target creature with flying."
 *
 * Verifies both modes: the pump on any creature, and the flying-restricted destroy.
 */
class SarkhansResolveScenarioTest : ScenarioTestBase() {

    init {
        context("Sarkhan's Resolve") {

            test("mode 1 gives target creature +3/+3 until end of turn") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Sarkhan's Resolve")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                val cast = game.castSpellWithMode(1, "Sarkhan's Resolve", modeIndex = 0, targetId = bears)
                withClue("Casting mode 1 should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("Grizzly Bears (2/2) should become 5/5") {
                    game.state.projectedState.getPower(bears) shouldBe 5
                    game.state.projectedState.getToughness(bears) shouldBe 5
                }
            }

            test("mode 2 destroys a creature with flying") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Sarkhan's Resolve")
                    .withCardOnBattlefield(2, "Wind Drake")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val drake = game.findPermanent("Wind Drake")!!

                val cast = game.castSpellWithMode(1, "Sarkhan's Resolve", modeIndex = 1, targetId = drake)
                withClue("Casting mode 2 against a flyer should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("Wind Drake should be destroyed") {
                    game.isOnBattlefield("Wind Drake") shouldBe false
                }
                withClue("Wind Drake should be in its owner's graveyard") {
                    game.findCardsInGraveyard(2, "Wind Drake").size shouldBe 1
                }
            }

            test("mode 2 cannot target a creature without flying") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Sarkhan's Resolve")
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                val cast = game.castSpellWithMode(1, "Sarkhan's Resolve", modeIndex = 1, targetId = bears)
                withClue("Destroy mode should reject a non-flying creature target") {
                    (cast.error != null) shouldBe true
                }
            }
        }
    }
}
