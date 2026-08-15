package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Light of Judgment — {4}{R} Instant: "deals 6 damage to target creature. Destroy up to one
 * Equipment attached to that creature."
 *
 * Exercises the new [com.wingedsheep.sdk.scripting.effects.CardSource.AttachedTo] gather: the
 * non-targeted "up to one" destroy is a Gather (Equipment attached to the damaged creature) →
 * choose-up-to-1 → destroy pipeline. The "up to one" choice is genuinely optional, so the
 * destroy is proven both ways (select the Equipment vs. decline).
 */
class LightOfJudgmentScenarioTest : ScenarioTestBase() {

    init {
        context("Light of Judgment") {
            test("deals 6 damage to the target and destroys the chosen attached Equipment") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Light of Judgment")
                    .withLandsOnBattlefield(1, "Mountain", 5)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardAttachedTo(2, "Bonesplitter", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!
                val sword = game.findPermanent("Bonesplitter")!!

                val cast = game.castSpell(1, "Light of Judgment", bears)
                withClue("Cast should succeed: ${cast.error}") { cast.error shouldBe null }
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                // Pipeline pauses to choose up to one Equipment attached to the creature.
                withClue("Should prompt to choose the attached Equipment") {
                    game.hasPendingDecision() shouldBe true
                }
                game.selectCards(listOf(sword))
                game.resolveStack()

                withClue("6 damage kills the 2/2 creature") {
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
                withClue("The chosen Equipment is destroyed") {
                    game.isInGraveyard(2, "Bonesplitter") shouldBe true
                }
            }

            test("declining the destroy leaves the Equipment on the battlefield") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Light of Judgment")
                    .withLandsOnBattlefield(1, "Mountain", 5)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withCardAttachedTo(2, "Bonesplitter", "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                val cast = game.castSpell(1, "Light of Judgment", bears)
                withClue("Cast should succeed: ${cast.error}") { cast.error shouldBe null }
                if (game.hasPendingDecision()) game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("Should prompt to choose up to one Equipment") {
                    game.hasPendingDecision() shouldBe true
                }
                game.skipSelection()
                game.resolveStack()

                withClue("Creature still dies to 6 damage") {
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                }
                withClue("Declined Equipment stays on the battlefield (just unattached)") {
                    game.isInGraveyard(2, "Bonesplitter") shouldBe false
                    (game.findPermanent("Bonesplitter") != null) shouldBe true
                }
            }
        }
    }
}
