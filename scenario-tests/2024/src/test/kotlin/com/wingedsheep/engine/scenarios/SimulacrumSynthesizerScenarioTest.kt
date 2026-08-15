package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.dsl.card
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Simulacrum Synthesizer (BIG #6).
 *
 * Simulacrum Synthesizer — {2}{U} Artifact.
 *   "When this artifact enters, scry 2.
 *    Whenever another artifact you control with mana value 3 or greater enters, create a 0/0
 *    colorless Construct artifact creature token with 'This token gets +1/+1 for each artifact
 *    you control.'"
 */
class SimulacrumSynthesizerScenarioTest : ScenarioTestBase() {

    private val projector = StateProjector()

    init {
        // MV 3 colorless artifact — should trigger the Construct token.
        val bigArtifact = card("Test MV3 Artifact") {
            manaCost = "{3}"
            typeLine = "Artifact"
        }
        // MV 2 colorless artifact — below threshold, should NOT trigger.
        val smallArtifact = card("Test MV2 Artifact") {
            manaCost = "{2}"
            typeLine = "Artifact"
        }
        cardRegistry.register(listOf(bigArtifact, smallArtifact))

        test("ETB scrys 2") {
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "Simulacrum Synthesizer")
                .withLandsOnBattlefield(1, "Island", 3)
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(1, "Island")
                .build()

            game.castSpell(1, "Simulacrum Synthesizer").error shouldBe null
            game.resolveStack()

            val decision = game.getPendingDecision()
            withClue("scry 2 presents a selection over the top two cards") {
                (decision is SelectCardsDecision) shouldBe true
                (decision as SelectCardsDecision).options.size shouldBe 2
            }
            game.skipSelection() // keep both on top
            game.resolveStack()

            game.isOnBattlefield("Simulacrum Synthesizer") shouldBe true
        }

        test("an MV3+ artifact entering creates a 0/0 Construct token") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Simulacrum Synthesizer")
                .withCardInHand(1, "Test MV3 Artifact")
                .withLandsOnBattlefield(1, "Island", 3)
                .withCardInLibrary(1, "Island")
                .build()

            game.castSpell(1, "Test MV3 Artifact").error shouldBe null
            game.resolveStack()

            val tokens = game.findPermanents("Construct Token")
            withClue("exactly one Construct token created") {
                tokens.size shouldBe 1
            }

            // Artifacts controlled now: Synthesizer, Test MV3 Artifact, the Construct token
            // itself = 3 → token gets +3/+3 on its 0/0 base.
            val projected = projector.project(game.state)
            withClue("token gets +1/+1 for each artifact you control (3 artifacts)") {
                projected.getPower(tokens.single()) shouldBe 3
                projected.getToughness(tokens.single()) shouldBe 3
            }
        }

        test("an MV2 artifact entering does NOT create a token") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Simulacrum Synthesizer")
                .withCardInHand(1, "Test MV2 Artifact")
                .withLandsOnBattlefield(1, "Island", 3)
                .withCardInLibrary(1, "Island")
                .build()

            game.castSpell(1, "Test MV2 Artifact").error shouldBe null
            game.resolveStack()

            withClue("MV2 is below the 'mana value 3 or greater' threshold") {
                game.findPermanents("Construct Token").size shouldBe 0
            }
        }

        test("the Synthesizer entering does not trigger its own token clause (another artifact)") {
            val game = scenario()
                .withPlayers()
                .withCardInHand(1, "Simulacrum Synthesizer")
                .withLandsOnBattlefield(1, "Island", 3)
                .withCardInLibrary(1, "Island")
                .withCardInLibrary(1, "Island")
                .build()

            game.castSpell(1, "Simulacrum Synthesizer").error shouldBe null
            game.resolveStack()
            if (game.hasPendingDecision()) game.skipSelection()
            game.resolveStack()

            withClue("'another artifact' excludes the Synthesizer itself") {
                game.findPermanents("Construct Token").size shouldBe 0
            }
        }
    }
}
