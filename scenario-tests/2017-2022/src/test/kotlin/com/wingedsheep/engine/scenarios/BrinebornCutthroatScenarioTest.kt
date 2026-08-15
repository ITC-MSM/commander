package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Brineborn Cutthroat ({1}{U}, 2/1 Merfolk Pirate, Flash).
 *
 * Whenever you cast a spell during an opponent's turn, put a +1/+1 counter on this creature.
 *
 * Verifies the [com.wingedsheep.sdk.dsl.Triggers.YouCastSpell] trigger gated by
 * [com.wingedsheep.sdk.dsl.Conditions.IsNotYourTurn]: it grows only when its controller casts
 * a spell on a turn that isn't theirs, and stays put for spells cast on their own turn.
 */
class BrinebornCutthroatScenarioTest : ScenarioTestBase() {

    // A {0} instant used to represent "casting a spell". Flash-timed by nature (instant).
    private val flashProbe = card("Flash Probe") {
        manaCost = "{0}"
        typeLine = "Instant"
        oracleText = "You gain 1 life."
        spell {
            effect = Effects.GainLife(1)
        }
    }

    init {
        cardRegistry.register(flashProbe)

        context("Brineborn Cutthroat") {

            test("grows when you cast a spell during an opponent's turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Brineborn Cutthroat")
                    .withCardInHand(1, "Flash Probe")
                    .withActivePlayer(2) // opponent's turn
                    .withPriorityPlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cutthroat = game.findPermanent("Brineborn Cutthroat")!!
                withClue("base 2/1 before casting") {
                    game.state.projectedState.getPower(cutthroat) shouldBe 2
                    game.state.projectedState.getToughness(cutthroat) shouldBe 1
                }

                game.castSpell(1, "Flash Probe").error shouldBe null
                game.resolveStack()

                withClue("casting on the opponent's turn adds a +1/+1 counter → 3/2") {
                    game.state.projectedState.getPower(cutthroat) shouldBe 3
                    game.state.projectedState.getToughness(cutthroat) shouldBe 2
                }
            }

            test("does not grow when you cast a spell during your own turn") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Brineborn Cutthroat")
                    .withCardInHand(1, "Flash Probe")
                    .withActivePlayer(1) // your own turn
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cutthroat = game.findPermanent("Brineborn Cutthroat")!!

                game.castSpell(1, "Flash Probe").error shouldBe null
                game.resolveStack()

                withClue("casting on your own turn does not trigger — stays 2/1") {
                    game.state.projectedState.getPower(cutthroat) shouldBe 2
                    game.state.projectedState.getToughness(cutthroat) shouldBe 1
                }
            }
        }
    }
}
