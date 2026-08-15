package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Xenic Poltergeist (ATQ #20).
 *
 * {1}{B}{B} Creature — Spirit 1/1
 * "{T}: Until your next upkeep, target noncreature artifact becomes an artifact creature with
 *  power and toughness each equal to its mana value."
 *
 * Proves the dynamic-P/T animate: the new [com.wingedsheep.sdk.dsl.Effects].BecomeCreatureWithManaValueStats
 * facade over the extended `BecomeCreatureEffect` (optional dynamic P/T). Millstone (mana value 2)
 * becomes a 2/2 artifact creature until the controller's next upkeep, then reverts to a noncreature
 * artifact.
 */
class XenicPoltergeistScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    init {
        context("Xenic Poltergeist") {

            test("animates a noncreature artifact to a creature with P/T = its mana value, then reverts at the controller's next upkeep") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    // P1's untapped Xenic Poltergeist, and a noncreature artifact Millstone (mana value 2).
                    .withCardOnBattlefield(1, "Xenic Poltergeist", summoningSickness = false)
                    .withCardOnBattlefield(1, "Millstone")
                    // Give both players library cards so the draw steps don't cause a deck-out loss.
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(1, "Plains")
                    .withCardInLibrary(2, "Island")
                    .withCardInLibrary(2, "Island")
                    .withCardInLibrary(2, "Island")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val millstone = game.findPermanent("Millstone")!!

                // Before: Millstone is a noncreature artifact.
                val before = stateProjector.project(game.state)
                withClue("Millstone starts as a noncreature artifact") {
                    before.isCreature(millstone) shouldBe false
                }

                val xenic = game.findPermanent("Xenic Poltergeist")!!
                val ability = cardRegistry.getCard("Xenic Poltergeist")!!.script.activatedAbilities[0]
                val result = game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = xenic,
                        abilityId = ability.id,
                        targets = listOf(ChosenTarget.Permanent(millstone))
                    )
                )
                withClue("Activating Xenic Poltergeist should succeed: ${result.error}") {
                    result.error shouldBe null
                }
                game.resolveStack()

                // After resolution: Millstone is a 2/2 artifact creature.
                val animated = stateProjector.project(game.state)
                withClue("Millstone is now a creature") {
                    animated.isCreature(millstone) shouldBe true
                }
                withClue("Millstone (mana value 2) is a 2/2") {
                    animated.getPower(millstone) shouldBe 2
                    animated.getToughness(millstone) shouldBe 2
                }
                withClue("Millstone is still an artifact") {
                    animated.hasType(millstone, "ARTIFACT") shouldBe true
                }

                // Advance through the opponent's turn — the effect is "until YOUR next upkeep", so it
                // stays active during the opponent's upkeep.
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)   // P2's upkeep
                withClue("Still animated during the opponent's turn (until *your* next upkeep)") {
                    game.state.activePlayerId shouldBe game.player2Id
                    stateProjector.project(game.state).isCreature(millstone) shouldBe true
                }

                // Advance to P1's *own* next upkeep, where the effect expires.
                var guard = 0
                while (!(game.state.activePlayerId == game.player1Id && game.state.step == Step.UPKEEP) && guard < 200) {
                    game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                    guard++
                }
                withClue("reached P1's upkeep") { game.state.activePlayerId shouldBe game.player1Id }
                game.resolveStack()

                val reverted = stateProjector.project(game.state)
                withClue("At the controller's next upkeep, Millstone is no longer a creature") {
                    reverted.isCreature(millstone) shouldBe false
                }
                withClue("Millstone is still an artifact after reverting") {
                    reverted.hasType(millstone, "ARTIFACT") shouldBe true
                }
            }
        }
    }
}
