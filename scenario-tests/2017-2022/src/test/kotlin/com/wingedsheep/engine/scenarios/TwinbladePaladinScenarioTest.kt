package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Twinblade Paladin ({3}{W}, 3/3 Human Knight).
 *
 * - Whenever you gain life, put a +1/+1 counter on this creature.
 * - As long as you have 25 or more life, this creature has double strike.
 *
 * Verifies the [com.wingedsheep.sdk.dsl.Triggers.YouGainLife] grow trigger and the
 * [com.wingedsheep.sdk.dsl.Conditions.LifeAtLeast] (25) conditional double-strike static
 * ability, which turns on and off as the controller's life crosses the threshold.
 */
class TwinbladePaladinScenarioTest : ScenarioTestBase() {

    // A {0} sorcery that makes its controller gain 3 life — a clean way to fire a life-gain event.
    private val gainThreeLife = card("Gain Three Life") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        oracleText = "You gain 3 life."
        spell {
            effect = Effects.GainLife(3)
        }
    }

    init {
        cardRegistry.register(gainThreeLife)

        context("Twinblade Paladin") {

            test("gaining life puts a +1/+1 counter on it") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Twinblade Paladin")
                    .withCardInHand(1, "Gain Three Life")
                    .withLifeTotal(1, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val paladin = game.findPermanent("Twinblade Paladin")!!
                withClue("base 3/3 before any life gain") {
                    game.state.projectedState.getPower(paladin) shouldBe 3
                    game.state.projectedState.getToughness(paladin) shouldBe 3
                }

                game.castSpell(1, "Gain Three Life").error shouldBe null
                game.resolveStack()

                withClue("gaining 3 life → one +1/+1 counter → 4/4") {
                    game.getLifeTotal(1) shouldBe 23
                    game.state.projectedState.getPower(paladin) shouldBe 4
                    game.state.projectedState.getToughness(paladin) shouldBe 4
                }
            }

            test("no double strike below 25 life") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Twinblade Paladin")
                    .withLifeTotal(1, 24)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val paladin = game.findPermanent("Twinblade Paladin")!!
                withClue("24 life is below the 25-life threshold") {
                    game.state.projectedState.hasKeyword(paladin, Keyword.DOUBLE_STRIKE) shouldBe false
                }
            }

            test("double strike at 25 or more life") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Twinblade Paladin")
                    .withLifeTotal(1, 25)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val paladin = game.findPermanent("Twinblade Paladin")!!
                withClue("25 life meets the threshold → double strike is granted") {
                    game.state.projectedState.hasKeyword(paladin, Keyword.DOUBLE_STRIKE) shouldBe true
                }
            }
        }
    }
}
