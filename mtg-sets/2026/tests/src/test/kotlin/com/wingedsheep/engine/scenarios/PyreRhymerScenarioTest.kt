package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.PreparedComponent
import com.wingedsheep.engine.state.components.battlefield.PreparedSpellCopyComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Pyre Rhymer // Molten Tide (Reality Fracture).
 *
 * Molten Tide is a one-sided High Tide: an `AdditionalManaOnSourceTap` static granted to the caster
 * until end of turn over "Mountains you control". The tests prove the bonus reaches the caster's
 * Mountains and does not reach the opponent's.
 */
class PyreRhymerScenarioTest : ScenarioTestBase() {

    private fun TestGame.prepareCopy(): EntityId =
        state.getExile(player1Id).single { id ->
            val e = state.getEntity(id)
            e?.get<CardComponent>()?.name == "Pyre Rhymer" && e.get<PreparedSpellCopyComponent>() != null
        }

    private fun castRhymerAndMoltenTide(): TestGame {
        var b = scenario()
            .withPlayers("Player", "Opponent")
            .withCardInHand(1, "Pyre Rhymer")
            .withCardInHand(1, "Lightning Strike")
            .withLandsOnBattlefield(1, "Mountain", 5)
            .withCardInHand(2, "Lightning Strike")
            .withLandsOnBattlefield(2, "Mountain", 1)
            .withActivePlayer(1)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        repeat(5) { b = b.withCardInLibrary(1, "Mountain") }
        repeat(5) { b = b.withCardInLibrary(2, "Mountain") }
        val game = b.build()

        game.castSpell(1, "Pyre Rhymer").error shouldBe null
        game.resolveStack()
        val rhymer = game.findPermanent("Pyre Rhymer")!!
        withClue("Pyre Rhymer enters prepared") {
            game.state.getEntity(rhymer)?.get<PreparedComponent>() shouldNotBe null
        }

        // One Mountain pays for Molten Tide; one stays untapped.
        game.execute(CastSpell(game.player1Id, game.prepareCopy(), emptyList(), faceIndex = 0)).error shouldBe null
        game.resolveStack()
        withClue("Casting Molten Tide unprepared the Rhymer") {
            game.state.getEntity(rhymer)?.get<PreparedComponent>() shouldBe null
        }
        return game
    }

    init {
        context("Molten Tide") {

            test("a Mountain you tap adds an additional {R} for the rest of the turn") {
                val game = castRhymerAndMoltenTide()

                withClue("Prowess: Molten Tide is a noncreature spell, so the Rhymer is 4/4") {
                    val rhymer = game.findPermanent("Pyre Rhymer")!!
                    game.state.projectedState.getPower(rhymer) shouldBe 4
                }

                withClue("The one remaining Mountain makes {R}{R}, enough for Lightning Strike's {1}{R}") {
                    game.castSpellTargetingPlayer(1, "Lightning Strike", 2).error shouldBe null
                    game.resolveStack()
                    game.getLifeTotal(2) shouldBe 17
                }
            }

            test("the opponent's Mountains get no bonus") {
                val game = castRhymerAndMoltenTide()

                // Hand priority to the opponent in the same step, with the grant still live.
                game.passPriority()
                game.state.priorityPlayerId shouldBe game.player2Id

                withClue("The opponent's single Mountain makes only {R}, so {1}{R} can't be paid") {
                    game.castSpellTargetingPlayer(2, "Lightning Strike", 1).error shouldNotBe null
                }
            }
        }
    }
}
