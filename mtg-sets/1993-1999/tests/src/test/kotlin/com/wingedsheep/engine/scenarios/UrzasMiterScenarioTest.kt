package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Urza's Miter (ATQ #76).
 *
 * {3} Artifact — "Whenever an artifact you control is put into a graveyard from the battlefield, if
 * it wasn't sacrificed, you may pay {3}. If you do, draw a card."
 *
 * Exercises the new sacrifice-cause signal: a non-sacrifice death (destruction) fires the trigger,
 * but sacrificing the same artifact does not (the `ZoneChangeEvent.wasSacrificed` flag stamped by
 * the central sacrifice hook, gated on the trigger via `excludeSacrifice = true`).
 */
class UrzasMiterScenarioTest : ScenarioTestBase() {

    // {0} sorcery that destroys a target permanent (a non-sacrifice death).
    private val slayPermanent = card("Slay Permanent") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        oracleText = "Destroy target permanent."
        spell {
            val t = target("target permanent", Targets.Permanent)
            effect = Effects.Destroy(t)
        }
    }

    // {0} sorcery that sacrifices a target artifact you control (a sacrifice death).
    private val offerArtifact = card("Offer Artifact") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        oracleText = "Sacrifice target artifact you control."
        spell {
            val t = target("target artifact you control", Targets.Artifact)
            effect = Effects.SacrificeTarget(t)
        }
    }

    init {
        cardRegistry.register(slayPermanent)
        cardRegistry.register(offerArtifact)

        context("Urza's Miter") {

            test("a non-sacrifice death triggers the may-pay {3} → draw a card") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Urza's Miter")
                    .withCardOnBattlefield(1, "Ashnod's Altar")
                    .withCardInHand(1, "Slay Permanent")
                    .withCardInLibrary(1, "Mountain") // something to draw
                    .withLandsOnBattlefield(1, "Mountain", 3) // pays the {3}
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)
                val altar = game.findPermanent("Ashnod's Altar")!!
                game.castSpell(1, "Slay Permanent", altar).error shouldBe null
                game.resolveStack()

                withClue("Ashnod's Altar should be destroyed (not sacrificed)") {
                    game.isOnBattlefield("Ashnod's Altar") shouldBe false
                }
                withClue("Urza's Miter offers the may-pay {3} decision on a non-sacrifice death") {
                    game.hasPendingDecision() shouldBe true
                }

                // handBefore counts the Slay Permanent we cast (now resolved away).
                game.answerYesNo(true)
                game.submitManaSourcesAutoPay()
                game.resolveStack()

                withClue("Paying {3} draws a card") {
                    // Started with hand = [Slay Permanent]; cast it (−1), then drew (+1) → handBefore.
                    game.handSize(1) shouldBe handBefore
                    game.isOnBattlefield("Mountain") shouldBe true
                }
            }

            test("a sacrificed artifact does NOT trigger Urza's Miter") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Urza's Miter")
                    .withCardOnBattlefield(1, "Ashnod's Altar")
                    .withCardInHand(1, "Offer Artifact")
                    .withCardInLibrary(1, "Mountain")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val altar = game.findPermanent("Ashnod's Altar")!!
                game.castSpell(1, "Offer Artifact", altar).error shouldBe null
                game.resolveStack()

                withClue("Ashnod's Altar should be sacrificed") {
                    game.isOnBattlefield("Ashnod's Altar") shouldBe false
                }
                withClue("Urza's Miter must NOT offer a may-pay decision for a sacrifice") {
                    game.hasPendingDecision() shouldBe false
                }
            }
        }
    }
}
