package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Angel of Vitality (M20 #4).
 *
 * "{2}{W} Creature — Angel 2/2
 *  Flying
 *  If you would gain life, you gain that much life plus 1 instead.
 *  This creature gets +2/+2 as long as you have 25 or more life."
 */
class AngelOfVitalityScenarioTest : ScenarioTestBase() {

    // A minimal sorcery that gives the caster 3 life. Used to drive the +1 life rider.
    private val gainThreeLife = card("Test Gain Three") {
        manaCost = "{W}"
        typeLine = "Sorcery"
        spell { effect = Effects.GainLife(3) }
    }

    private val stateProjector = StateProjector()

    init {
        cardRegistry.register(gainThreeLife)

        context("Angel of Vitality") {

            test("gaining 3 life actually gains 4 while Angel of Vitality is on the battlefield") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Angel of Vitality")
                    .withCardInHand(1, "Test Gain Three")
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cast = game.castSpell(1, "Test Gain Three")
                withClue("Casting the test spell should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                withClue("Gaining 3 life with Angel of Vitality should net 4") {
                    game.getLifeTotal(1) shouldBe 24
                }
            }

            test("Angel of Vitality is a 4/4 at 25 or more life and a 2/2 below 25") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Angel of Vitality", summoningSickness = false)
                    .withLifeTotal(1, 25)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val angelId = game.findPermanent("Angel of Vitality")!!
                val projectedHigh = stateProjector.project(game.state)
                withClue("At life=25 the +2/+2 bonus should be active (4/4)") {
                    projectedHigh.getPower(angelId) shouldBe 4
                    projectedHigh.getToughness(angelId) shouldBe 4
                }

                withClue("Angel of Vitality should have flying") {
                    projectedHigh.hasKeyword(angelId, Keyword.FLYING) shouldBe true
                }
            }

            test("Angel of Vitality is a 2/2 while below 25 life") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Angel of Vitality", summoningSickness = false)
                    .withLifeTotal(1, 24)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val angelId = game.findPermanent("Angel of Vitality")!!
                val projected = stateProjector.project(game.state)
                withClue("At life=24 the bonus should be inactive (2/2)") {
                    projected.getPower(angelId) shouldBe 2
                    projected.getToughness(angelId) shouldBe 2
                }
            }
        }
    }
}
