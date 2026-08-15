package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for Raubahn, Bull of Ala Mhigo (FIN #151).
 *
 * Raubahn, Bull of Ala Mhigo {1}{R} Legendary Creature — Human Warrior 2/2
 * Ward—Pay life equal to Raubahn's power.
 * Whenever Raubahn attacks, attach up to one target Equipment you control to target attacking
 * creature.
 *
 * The ward cost is a dynamic life cost (`WardCost.DynamicLife(DynamicAmounts.sourcePower())`):
 * the amount is Raubahn's power read when the ward trigger resolves (CR 702.21b / Scryfall
 * ruling 2025-06-06). These tests prove the base-power amount, the *modified*-power amount (a
 * Glorious Anthem makes the cost 3), the can't-pay immediate counter, and the attack-trigger
 * attach (with and without choosing the optional Equipment target).
 */
class RaubahnBullOfAlaMhigoScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        test("ward cost equals Raubahn's base power (2) — paying lets the spell resolve") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Raubahn, Bull of Ala Mhigo")
                .withCardInHand(2, "Lightning Bolt")
                .withLandsOnBattlefield(2, "Mountain", 1)
                .withLifeTotal(2, 20)
                .withActivePlayer(2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val raubahn = game.findPermanent("Raubahn, Bull of Ala Mhigo")!!

            // Opponent (player 2) targets Raubahn → ward triggers, resolves, prompts the caster.
            game.castSpell(2, "Lightning Bolt", raubahn).error shouldBe null
            game.resolveStack()

            val decision = game.getPendingDecision()
            withClue("ward prompts the caster to pay life") {
                (decision is YesNoDecision) shouldBe true
            }
            decision!!.playerId shouldBe game.player2Id

            game.answerYesNo(true).error shouldBe null
            game.resolveStack()

            withClue("paid 2 life (Raubahn's power), Bolt resolves and kills the 2/2") {
                game.getLifeTotal(2) shouldBe 18
                game.findPermanent("Raubahn, Bull of Ala Mhigo") shouldBe null
            }
        }

        test("ward cost tracks Raubahn's MODIFIED power — anthem makes it 3 life") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Raubahn, Bull of Ala Mhigo")
                .withCardOnBattlefield(1, "Glorious Anthem")   // +1/+1 → Raubahn is 3/3
                .withCardInHand(2, "Lightning Bolt")
                .withLandsOnBattlefield(2, "Mountain", 1)
                .withLifeTotal(2, 20)
                .withActivePlayer(2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val raubahn = game.findPermanent("Raubahn, Bull of Ala Mhigo")!!
            withClue("anthem makes Raubahn 3/3") {
                stateProjector.project(game.state).getPower(raubahn) shouldBe 3
            }

            game.castSpell(2, "Lightning Bolt", raubahn).error shouldBe null
            game.resolveStack()

            (game.getPendingDecision() is YesNoDecision) shouldBe true
            game.answerYesNo(true).error shouldBe null
            game.resolveStack()

            withClue("paid 3 life (Raubahn's modified power)") {
                game.getLifeTotal(2) shouldBe 17
            }
        }

        test("ward counters immediately when the caster can't pay the dynamic life cost") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Raubahn, Bull of Ala Mhigo")
                .withCardOnBattlefield(1, "Glorious Anthem")   // Raubahn 3/3 → ward costs 3 life
                .withCardInHand(2, "Lightning Bolt")
                .withLandsOnBattlefield(2, "Mountain", 1)
                .withLifeTotal(2, 2)                            // can't pay 3
                .withActivePlayer(2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val raubahn = game.findPermanent("Raubahn, Bull of Ala Mhigo")!!

            game.castSpell(2, "Lightning Bolt", raubahn).error shouldBe null
            game.resolveStack()

            withClue("no decision — 2 life < 3, ward counters the Bolt immediately") {
                game.hasPendingDecision() shouldBe false
            }
            withClue("Raubahn survives, no life paid") {
                game.findPermanent("Raubahn, Bull of Ala Mhigo") shouldNotBe null
                game.getLifeTotal(2) shouldBe 2
            }
        }

        test("attack trigger attaches the chosen Equipment to the chosen attacking creature") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Raubahn, Bull of Ala Mhigo", summoningSickness = false)
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false) // second attacker
                .withCardOnBattlefield(1, "Buster Sword")   // Equipment to move (+3/+2)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val sword = game.findPermanent("Buster Sword")!!
            val bears = game.findPermanent("Grizzly Bears")!!

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Raubahn, Bull of Ala Mhigo" to 2, "Grizzly Bears" to 2)).error shouldBe null
            game.resolveStack()

            val decision = game.getPendingDecision()
            withClue("attack trigger pauses to choose Equipment (slot 0) + attacking creature (slot 1)") {
                decision shouldNotBe null
            }
            game.submitDecision(TargetsResponse(decision!!.id, mapOf(0 to listOf(sword), 1 to listOf(bears))))
            game.resolveStack()

            withClue("Buster Sword now attached to Grizzly Bears (2/2 +3/+2 = 5/4)") {
                stateProjector.project(game.state).getPower(bears) shouldBe 5
                stateProjector.project(game.state).getToughness(bears) shouldBe 4
            }
        }

        test("attack trigger is a no-op when the optional Equipment target is declined") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Raubahn, Bull of Ala Mhigo", summoningSickness = false)
                .withCardOnBattlefield(1, "Buster Sword")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val raubahn = game.findPermanent("Raubahn, Bull of Ala Mhigo")!!
            val sword = game.findPermanent("Buster Sword")!!

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Raubahn, Bull of Ala Mhigo" to 2)).error shouldBe null
            game.resolveStack()

            val decision = game.getPendingDecision()
            withClue("attack trigger still pauses to choose targets") {
                decision shouldNotBe null
            }
            // Decline the "up to one" Equipment (slot 0 empty); required attacking creature = Raubahn.
            game.submitDecision(TargetsResponse(decision!!.id, mapOf(0 to emptyList(), 1 to listOf(raubahn))))
            game.resolveStack()

            withClue("no attach happened — Buster Sword stays unattached, Raubahn stays 2/2") {
                stateProjector.project(game.state).getPower(raubahn) shouldBe 2
                stateProjector.project(game.state).getToughness(raubahn) shouldBe 2
                game.findPermanent("Buster Sword") shouldNotBe null
            }
        }
    }
}
