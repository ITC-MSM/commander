package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Arni, Renowned Champion — "Whenever another creature you control enters, Arni gets +X/+0
 * until end of turn, where X is that creature's power."
 *
 * X is read off the entered creature (EntityReference.Triggering) on resolution.
 */
class ArniRenownedChampionScenarioTest : ScenarioTestBase() {
    init {
        test("a creature you control entering gives Arni +X/+0 equal to its power") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Arni, Renowned Champion")
                .withLandsOnBattlefield(1, "Mountain", 4)
                .withCardInHand(1, "Hill Giant")
                .withCardInLibrary(1, "Mountain")
                .withCardInLibrary(2, "Mountain")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val arni = game.findPermanent("Arni, Renowned Champion")!!
            withClue("printed 1/5") {
                game.state.projectedState.getPower(arni) shouldBe 1
                game.state.projectedState.getToughness(arni) shouldBe 5
            }

            val result = game.castSpell(1, "Hill Giant")
            withClue("casting Hill Giant should succeed: ${result.error}") { result.error shouldBe null }
            if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
            game.resolveStack()

            withClue("Hill Giant is on the battlefield") { (game.findPermanent("Hill Giant") != null) shouldBe true }
            withClue("Arni gets +3/+0 from the 3-power Hill Giant") {
                game.state.projectedState.getPower(arni) shouldBe 4
                game.state.projectedState.getToughness(arni) shouldBe 5
            }
        }

        test("an opponent's creature entering does not pump Arni") {
            val game = scenario().withPlayers()
                .withCardOnBattlefield(1, "Arni, Renowned Champion")
                .withLandsOnBattlefield(2, "Mountain", 4)
                .withCardInHand(2, "Hill Giant")
                .withCardInLibrary(1, "Mountain")
                .withCardInLibrary(2, "Mountain")
                .withActivePlayer(2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val arni = game.findPermanent("Arni, Renowned Champion")!!
            val result = game.castSpell(2, "Hill Giant")
            withClue("casting Hill Giant should succeed: ${result.error}") { result.error shouldBe null }
            if (game.getPendingDecision() is SelectManaSourcesDecision) game.submitManaSourcesAutoPay()
            game.resolveStack()

            game.state.projectedState.getPower(arni) shouldBe 1
        }
    }
}
