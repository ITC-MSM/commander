package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Germinate Recruits (Reality Fracture #9) — {2}{W} Instant:
 *   Create X 2/2 colorless Wizard Soldier creature tokens named Cadet, where X is the amount of
 *   life you gained this turn.
 *
 * Pins that X reads the life gained this turn (not a fixed count), and that no life gained means
 * no tokens.
 */
class GerminateRecruitsScenarioTest : ScenarioTestBase() {
    init {
        test("creates one Cadet per point of life gained this turn") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Chaplain's Blessing")
                .withCardInHand(1, "Germinate Recruits")
                .withLandsOnBattlefield(1, "Plains", 4)
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(2, "Plains")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpell(1, "Chaplain's Blessing").error shouldBe null
            game.resolveStack()
            game.getLifeTotal(1) shouldBe 25

            game.castSpell(1, "Germinate Recruits").error shouldBe null
            game.resolveStack()

            val cadets = game.findPermanents("Cadet")
            withClue("5 life gained this turn -> five Cadets") {
                cadets.size shouldBe 5
            }
            cadets.forEach { cadet ->
                game.state.projectedState.getPower(cadet) shouldBe 2
                game.state.projectedState.getToughness(cadet) shouldBe 2
            }
        }

        test("no life gained this turn creates no Cadets") {
            val game = scenario().withPlayers()
                .withCardInHand(1, "Germinate Recruits")
                .withLandsOnBattlefield(1, "Plains", 3)
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(2, "Plains")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN).build()

            game.castSpell(1, "Germinate Recruits").error shouldBe null
            game.resolveStack()

            game.findPermanents("Cadet").size shouldBe 0
            game.isInGraveyard(1, "Germinate Recruits") shouldBe true
        }
    }
}
