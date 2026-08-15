package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Ray of Ruin (LCI #117).
 *
 * "{4}{B} Sorcery — Exile target creature, Vehicle, or nonbasic land. Scry 1."
 *
 * Verifies the composed target filter (creature OR Vehicle OR nonbasic land)
 * exiles each valid target type, that a basic land is not a legal target, and
 * that Scry 1 runs after the exile.
 */
class RayOfRuinScenarioTest : ScenarioTestBase() {

    // Scry 1 surfaces a bottom-or-keep prompt (YesNoDecision) and possibly a
    // library reorder prompt; drain both without changing the library order.
    private fun TestGame.drainScry() {
        var guard = 0
        while (hasPendingDecision() && guard++ < 8) {
            when (getPendingDecision()) {
                is YesNoDecision -> answerYesNo(false)
                is ReorderLibraryDecision -> keepLibraryOrder()
                else -> skipSelection()
            }
        }
    }

    init {
        context("Ray of Ruin") {

            test("exiles a target creature, then scries") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Ray of Ruin")
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(3) { builder = builder.withCardInLibrary(1, "Forest") }
                val game = builder.build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val result = game.castSpell(1, "Ray of Ruin", bears)
                withClue("Casting Ray of Ruin targeting a creature should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()
                game.drainScry()

                withClue("The targeted creature was exiled") {
                    game.isInExile(2, "Grizzly Bears") shouldBe true
                    game.isOnBattlefield("Grizzly Bears") shouldBe false
                }
            }

            test("exiles a target Vehicle") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Ray of Ruin")
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withCardOnBattlefield(2, "Careening Mine Cart")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(3) { builder = builder.withCardInLibrary(1, "Forest") }
                val game = builder.build()

                val cart = game.findPermanent("Careening Mine Cart")!!
                val result = game.castSpell(1, "Ray of Ruin", cart)
                withClue("Casting Ray of Ruin targeting a Vehicle should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()
                game.drainScry()

                withClue("The targeted Vehicle was exiled") {
                    game.isInExile(2, "Careening Mine Cart") shouldBe true
                    game.isOnBattlefield("Careening Mine Cart") shouldBe false
                }
            }

            test("exiles a target nonbasic land") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Ray of Ruin")
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withCardOnBattlefield(2, "Strip Mine")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(3) { builder = builder.withCardInLibrary(1, "Forest") }
                val game = builder.build()

                val stripMine = game.findPermanent("Strip Mine")!!
                val result = game.castSpell(1, "Ray of Ruin", stripMine)
                withClue("Casting Ray of Ruin targeting a nonbasic land should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()
                game.drainScry()

                withClue("The targeted nonbasic land was exiled") {
                    game.isInExile(2, "Strip Mine") shouldBe true
                    game.isOnBattlefield("Strip Mine") shouldBe false
                }
            }

            test("cannot target a basic land") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Ray of Ruin")
                    .withLandsOnBattlefield(1, "Swamp", 5)
                    .withLandsOnBattlefield(2, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val forest = game.findPermanent("Forest")!!
                val result = game.castSpell(1, "Ray of Ruin", forest)
                withClue("A basic land is not a legal target for Ray of Ruin") {
                    (result.error != null) shouldBe true
                }
            }
        }
    }
}
