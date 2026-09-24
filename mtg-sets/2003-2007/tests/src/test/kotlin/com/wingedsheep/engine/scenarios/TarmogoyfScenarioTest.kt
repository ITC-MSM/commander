package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Tarmogoyf (FUT #153, {1}{G}, star/1+star).
 *
 *   Tarmogoyf's power is equal to the number of card types among cards in all graveyards and its
 *   toughness is equal to that number plus 1.
 */
class TarmogoyfScenarioTest : ScenarioTestBase() {

    init {
        context("Tarmogoyf") {

            test("is 0/1 with empty graveyards") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Tarmogoyf")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val goyf = game.findPermanent("Tarmogoyf")!!
                val projected = game.state.projectedState
                projected.getPower(goyf) shouldBe 0
                projected.getToughness(goyf) shouldBe 1
            }

            test("counts distinct card types across every graveyard, not cards") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Tarmogoyf")
                    // Alice: artifact creature + creature
                    .withCardInGraveyard(1, "Ornithopter")
                    .withCardInGraveyard(1, "Grizzly Bears")
                    // Bob: instant, another instant, sorcery
                    .withCardInGraveyard(2, "Lightning Bolt")
                    .withCardInGraveyard(2, "Shock")
                    .withCardInGraveyard(2, "Divination")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val goyf = game.findPermanent("Tarmogoyf")!!
                val projected = game.state.projectedState
                withClue("artifact, creature, instant, sorcery = 4") {
                    projected.getPower(goyf) shouldBe 4
                    projected.getToughness(goyf) shouldBe 5
                }
            }

            test("a burn spell that hits the graveyard first can grow it out of lethal range") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Tarmogoyf")
                    .withCardInGraveyard(1, "Grizzly Bears")
                    .withCardInGraveyard(2, "Ornithopter")
                    .withCardInHand(2, "Lightning Bolt")
                    .withLandsOnBattlefield(2, "Mountain", 1)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val goyf = game.findPermanent("Tarmogoyf")!!
                withClue("artifact + creature = 2/3 before the Bolt") {
                    game.state.projectedState.getToughness(goyf) shouldBe 3
                }

                game.castSpell(2, "Lightning Bolt", goyf).error shouldBe null
                game.resolveStack()

                withClue("Bolt is in the graveyard before SBAs, making Tarmogoyf 3/4 with 3 damage") {
                    game.isOnBattlefield("Tarmogoyf") shouldBe true
                    game.state.projectedState.getToughness(goyf) shouldBe 4
                }
            }
        }
    }
}
