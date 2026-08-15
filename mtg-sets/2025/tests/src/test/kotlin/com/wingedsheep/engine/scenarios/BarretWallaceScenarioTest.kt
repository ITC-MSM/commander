package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Barret Wallace — {3}{R} Legendary Creature — Human Rebel 4/4
 *   Reach
 *   Whenever Barret Wallace attacks, it deals damage equal to the number of equipped creatures
 *   you control to defending player.
 *
 * The attack trigger deals damage equal to the count of equipped creatures you control (creatures
 * with at least one Equipment attached, CR 301.5) to the defending player. We attach real Equipment
 * via the builder so the creatures read as "equipped"; an unequipped creature you control and an
 * opponent's equipped creature must not count.
 */
class BarretWallaceScenarioTest : ScenarioTestBase() {

    init {
        test("Barret's attack deals damage equal to the number of equipped creatures you control") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Barret Wallace")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withCardOnBattlefield(1, "Centaur Courser") // left unequipped — must not count
                .withCardOnBattlefield(2, "Hill Giant")       // opponent's creature
                // Two equipped creatures you control: Barret (Buster Sword) and Bears (Coral Sword).
                .withCardAttachedTo(1, "Buster Sword", "Barret Wallace")
                .withCardAttachedTo(1, "Coral Sword", "Grizzly Bears")
                // Opponent's equipped creature — must not count ("you control").
                .withCardAttachedTo(2, "Lion Heart", "Hill Giant")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val lifeBefore = game.getLifeTotal(2)

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Barret Wallace" to 2))
            // The attack trigger goes on the stack; resolve it.
            game.resolveStack()

            withClue("Defending player takes damage = 2 equipped creatures you control") {
                game.getLifeTotal(2) shouldBe (lifeBefore - 2)
            }
        }

        test("Barret deals no damage when you control no equipped creatures") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, "Barret Wallace")
                .withCardOnBattlefield(1, "Grizzly Bears")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val lifeBefore = game.getLifeTotal(2)

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf("Barret Wallace" to 2))
            game.resolveStack()

            withClue("No equipped creatures -> 0 damage from the trigger") {
                game.getLifeTotal(2) shouldBe lifeBefore
            }
        }
    }
}
