package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Kithkin Mourncaller (LRW #224, {2}{G}, Creature — Kithkin Scout 2/2).
 *
 *   Whenever an attacking Kithkin or Elf is put into your graveyard from the battlefield, you may
 *   draw a card.
 *
 * "Attacking" is only answerable from last-known information: a creature is removed from combat as
 * it leaves the battlefield (CR 506.4), so by the time the trigger is gated its `AttackingComponent`
 * is gone. These tests pin the LKI reading in both directions — the attacking death that triggers
 * and the non-attacking death that must not — plus the declined "may", the subtype scoping, and
 * Mourncaller's own death (there is no "another" on this card).
 */
class KithkinMourncallerScenarioTest : ScenarioTestBase() {

    init {
        context("Kithkin Mourncaller") {

            test("an attacking Elf dying draws a card") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Kithkin Mourncaller", summoningSickness = false)
                    .withCardOnBattlefield(1, "Llanowar Elves", summoningSickness = false)
                    .withCardOnBattlefield(2, "Serra Angel")
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Llanowar Elves" to 2))
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                // The 4/4 Angel eats the 1/1 Elf while it is attacking.
                game.declareBlockers(mapOf("Serra Angel" to listOf("Llanowar Elves")))
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.hasPendingDecision()) game.answerYesNo(true)
                game.resolveStack()

                game.isInGraveyard(1, "Llanowar Elves") shouldBe true
                withClue("the attacking Elf's death should have drawn a card") {
                    game.handSize(1) shouldBe handBefore + 1
                }
            }

            test("declining the may draws nothing") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Kithkin Mourncaller", summoningSickness = false)
                    .withCardOnBattlefield(1, "Llanowar Elves", summoningSickness = false)
                    .withCardOnBattlefield(2, "Serra Angel")
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Llanowar Elves" to 2))
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareBlockers(mapOf("Serra Angel" to listOf("Llanowar Elves")))
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.hasPendingDecision()) game.answerYesNo(false)
                game.resolveStack()

                game.isInGraveyard(1, "Llanowar Elves") shouldBe true
                game.handSize(1) shouldBe handBefore
            }

            test("a Kithkin or Elf that was not attacking does not trigger it") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Kithkin Mourncaller")
                    .withCardOnBattlefield(1, "Llanowar Elves")
                    .withCardInHand(2, "Shock")
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)

                game.castSpell(2, "Shock", game.findPermanent("Llanowar Elves")!!)
                game.resolveStack()

                game.isInGraveyard(1, "Llanowar Elves") shouldBe true
                withClue("the Elf was never attacking, so nothing should have triggered") {
                    game.hasPendingDecision() shouldBe false
                    game.handSize(1) shouldBe handBefore
                }
            }

            test("a non-Kithkin non-Elf attacker dying does not trigger it") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Kithkin Mourncaller", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardOnBattlefield(2, "Serra Angel")
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Grizzly Bears" to 2))
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareBlockers(mapOf("Serra Angel" to listOf("Grizzly Bears")))
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()

                game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                withClue("a Bear is neither a Kithkin nor an Elf") {
                    game.hasPendingDecision() shouldBe false
                    game.handSize(1) shouldBe handBefore
                }
            }

            test("Mourncaller's own attacking death triggers it — there is no \"another\"") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Kithkin Mourncaller", summoningSickness = false)
                    .withCardOnBattlefield(2, "Serra Angel")
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Kithkin Mourncaller" to 2))
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareBlockers(mapOf("Serra Angel" to listOf("Kithkin Mourncaller")))
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()
                if (game.hasPendingDecision()) game.answerYesNo(true)
                game.resolveStack()

                game.isInGraveyard(1, "Kithkin Mourncaller") shouldBe true
                game.handSize(1) shouldBe handBefore + 1
            }

            test("an opponent's attacking Elf dying does not trigger it") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Kithkin Mourncaller")
                    .withCardOnBattlefield(1, "Serra Angel")
                    .withCardOnBattlefield(2, "Llanowar Elves", summoningSickness = false)
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Llanowar Elves" to 1))
                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
                game.declareBlockers(mapOf("Serra Angel" to listOf("Llanowar Elves")))
                game.passUntilPhase(Phase.COMBAT, Step.COMBAT_DAMAGE)
                game.resolveStack()

                game.isInGraveyard(2, "Llanowar Elves") shouldBe true
                withClue("the Elf went to Bob's graveyard, not yours") {
                    game.hasPendingDecision() shouldBe false
                    game.handSize(1) shouldBe handBefore
                }
            }
        }
    }
}
