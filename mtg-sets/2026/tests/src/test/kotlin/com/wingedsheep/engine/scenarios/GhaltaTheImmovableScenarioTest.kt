package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Ghalta the Immovable (Reality Fracture #197) — {8}{W} Legendary Creature — Elder Dinosaur 0/7:
 *   This spell costs {X} less to cast, where X is the greatest toughness among creatures you control.
 *   Creatures you control can attack as though they didn't have defender.
 *   Each creature you control with toughness greater than its power assigns combat damage equal to
 *   its toughness rather than its power.
 *
 * The middle line is the battlefield-scoped `CanAttackDespiteDefender`: a Glacial Wall (0/7,
 * defender) attacks while its controller has Ghalta, and hits for its toughness.
 */
class GhaltaTheImmovableScenarioTest : ScenarioTestBase() {
    init {
        test("costs {X} less, where X is the greatest toughness among your creatures") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardInHand(1, "Ghalta the Immovable")
                .withCardOnBattlefield(1, "Glacial Wall")
                .withLandsOnBattlefield(1, "Plains", 2)
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            withClue("{8}{W} minus 7 generic is {1}{W}, payable with two Plains") {
                game.castSpell(1, "Ghalta the Immovable").error shouldBe null
            }
            game.resolveStack()
            game.isOnBattlefield("Ghalta the Immovable") shouldBe true
        }

        test("a defender you control attacks and assigns damage equal to its toughness") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Ghalta the Immovable")
                .withCardOnBattlefield(1, "Glacial Wall", summoningSickness = false)
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(2, "Plains")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Glacial Wall" to 2)).error shouldBe null
            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)
            game.declareNoBlockers()
            game.passUntilPhase(Phase.POSTCOMBAT_MAIN, Step.POSTCOMBAT_MAIN)

            withClue("the 0/7 wall hits for 7") {
                game.getLifeTotal(2) shouldBe 13
            }
        }

        test("without Ghalta the defender can't attack") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Glacial Wall", summoningSickness = false)
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(2, "Plains")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Glacial Wall" to 2)).error shouldNotBe null
        }

        test("an opponent's defender still can't attack") {
            val game = scenario()
                .withPlayers("Player1", "Player2")
                .withCardOnBattlefield(1, "Ghalta the Immovable")
                .withCardOnBattlefield(2, "Glacial Wall", summoningSickness = false)
                .withCardInLibrary(1, "Plains")
                .withCardInLibrary(2, "Plains")
                .withActivePlayer(2)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Glacial Wall" to 1)).error shouldNotBe null
        }
    }
}
