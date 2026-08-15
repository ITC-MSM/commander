package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Ancestor Dragon (GS1, reprinted in FDN) — {4}{W}{W} Creature — Dragon 5/6, Flying.
 *
 * "Whenever one or more creatures you control attack, you gain 1 life for each attacking creature."
 *
 * A single batch trigger (CR 508.3) that fires once per combat and gains life equal to the number
 * of attacking creatures you control at resolution.
 */
class AncestorDragonScenarioTest : ScenarioTestBase() {

    private val name = "Ancestor Dragon"

    init {
        test("attacking with two creatures gains 2 life (one per attacker), from a single trigger") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, name, summoningSickness = false)
                .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val lifeBefore = game.getLifeTotal(1)

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf(name to 2, "Grizzly Bears" to 2)).error shouldBe null
            game.resolveStack()

            withClue("1 life for each of the two attacking creatures") {
                game.getLifeTotal(1) shouldBe lifeBefore + 2
            }
        }

        test("attacking with only Ancestor Dragon gains 1 life") {
            val game = scenario()
                .withPlayers()
                .withCardOnBattlefield(1, name, summoningSickness = false)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()

            val lifeBefore = game.getLifeTotal(1)

            game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
            game.declareAttackers(mapOf(name to 2)).error shouldBe null
            game.resolveStack()

            withClue("a lone attacker gains exactly 1 life") {
                game.getLifeTotal(1) shouldBe lifeBefore + 1
            }
        }
    }
}
