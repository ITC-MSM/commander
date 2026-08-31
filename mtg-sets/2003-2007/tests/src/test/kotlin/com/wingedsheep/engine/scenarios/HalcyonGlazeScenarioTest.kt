package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Scenario test for Halcyon Glaze (RAV #54) — {1}{U}{U} Enchantment.
 *
 *   Whenever you cast a creature spell, this enchantment becomes a 4/4 Illusion creature with
 *   flying in addition to its other types until end of turn.
 *
 * "In addition to its other types" is the clause worth pinning: the animated Glaze must still be an
 * Enchantment, so enchantment removal keeps answering it. The other half is the trigger's scope —
 * it watches *creature* spells only, and only ones you cast.
 */
class HalcyonGlazeScenarioTest : ScenarioTestBase() {

    init {
        context("Halcyon Glaze") {

            test("casting a creature spell animates it into a 4/4 flying Illusion that stays an enchantment") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Halcyon Glaze")
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val glaze = game.findPermanent("Halcyon Glaze").shouldNotBeNull()

                withClue("Before the trigger it is a plain enchantment with no P/T") {
                    game.state.projectedState.isCreature(glaze) shouldBe false
                }

                game.castSpell(1, "Grizzly Bears").isSuccess shouldBe true
                game.resolveStack()

                val projected = game.state.projectedState
                withClue("It is now a 4/4 Illusion with flying") {
                    projected.isCreature(glaze) shouldBe true
                    projected.getPower(glaze) shouldBe 4
                    projected.getToughness(glaze) shouldBe 4
                    projected.hasKeyword(glaze, Keyword.FLYING) shouldBe true
                    projected.hasSubtype(glaze, "Illusion") shouldBe true
                }
                withClue("\"In addition to its other types\" — it is still an Enchantment") {
                    projected.hasType(glaze, "ENCHANTMENT") shouldBe true
                }
            }

            test("a noncreature spell does not animate it") {
                val game = scenario()
                    .withPlayers("P1", "P2")
                    .withCardOnBattlefield(1, "Halcyon Glaze")
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val glaze = game.findPermanent("Halcyon Glaze").shouldNotBeNull()

                val bears = game.findPermanent("Grizzly Bears").shouldNotBeNull()
                game.castSpell(1, "Lightning Bolt", bears).isSuccess shouldBe true
                game.resolveStack()

                withClue("The trigger reads \"creature spell\"") {
                    game.state.projectedState.isCreature(glaze) shouldBe false
                }
            }
        }
    }
}
