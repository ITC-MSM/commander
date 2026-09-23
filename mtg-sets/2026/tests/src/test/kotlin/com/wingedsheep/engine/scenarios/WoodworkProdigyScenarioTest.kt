package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.PreparedComponent
import com.wingedsheep.engine.state.components.battlefield.PreparedSpellCopyComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Woodwork Prodigy // Soul Tether (Reality Fracture).
 *
 * Covers the `StatePredicate.IsPrepared` intervening-if ("if this creature isn't prepared") and the
 * predefined Heartwood token its prepare spell creates.
 */
class WoodworkProdigyScenarioTest : ScenarioTestBase() {

    private fun TestGame.prepareCopies(): List<EntityId> =
        state.getExile(player1Id).filter { id ->
            val e = state.getEntity(id)
            e?.get<CardComponent>()?.name == "Woodwork Prodigy" && e.get<PreparedSpellCopyComponent>() != null
        }

    private fun builder(): ScenarioBuilder {
        var b = scenario()
            .withPlayers("Player", "Opponent")
            .withCardOnBattlefield(1, "Woodwork Prodigy")
            .withLandsOnBattlefield(1, "Mountain", 3)
            .withCardInHand(1, "Shock")
            .withActivePlayer(2)
            .inPhase(Phase.ENDING, Step.END)
        repeat(6) { b = b.withCardInLibrary(1, "Mountain") }
        repeat(6) { b = b.withCardInLibrary(2, "Forest") }
        return b
    }

    init {
        context("Woodwork Prodigy") {

            test("an unprepared Prodigy becomes prepared in your upkeep; Soul Tether makes a Heartwood that taps for red") {
                val game = builder().build()
                val prodigy = game.findPermanent("Woodwork Prodigy")!!

                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.state.activePlayerId shouldBe game.player1Id
                game.resolveStack()

                withClue("It isn't prepared, so the upkeep trigger prepares it") {
                    game.state.getEntity(prodigy)?.get<PreparedComponent>() shouldNotBe null
                    game.prepareCopies().size shouldBe 1
                }

                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                val copyId = game.prepareCopies().single()
                game.execute(CastSpell(game.player1Id, copyId, emptyList(), faceIndex = 0)).error shouldBe null
                game.resolveStack()

                val heartwood = game.findPermanent("Heartwood")
                withClue("Soul Tether created a Heartwood token") { heartwood shouldNotBe null }
                val projected = game.state.projectedState
                withClue("Heartwood is a red and green artifact") {
                    projected.hasType(heartwood!!, "ARTIFACT") shouldBe true
                    projected.isCreature(heartwood) shouldBe false
                    projected.getColors(heartwood) shouldBe setOf(Color.RED.name, Color.GREEN.name)
                }
                withClue("Casting the copy unprepared the Prodigy") {
                    game.state.getEntity(prodigy)?.get<PreparedComponent>() shouldBe null
                }

                // All three Mountains paid for Soul Tether; the Heartwood alone pays for Shock's {R}.
                withClue("Heartwood's mana ability produces red") {
                    game.castSpellTargetingPlayer(1, "Shock", 2).error shouldBe null
                    game.resolveStack()
                    game.getLifeTotal(2) shouldBe 18
                }
            }

            test("a Prodigy that is already prepared doesn't trigger in the next upkeep") {
                val game = builder().build()
                val prodigy = game.findPermanent("Woodwork Prodigy")!!

                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.resolveStack()
                game.state.getEntity(prodigy)?.get<PreparedComponent>() shouldNotBe null

                // Through the opponent's turn to our next upkeep, leaving the copy uncast.
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.state.activePlayerId shouldBe game.player2Id
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.state.activePlayerId shouldBe game.player1Id

                withClue("The intervening-if fails, so nothing goes on the stack") {
                    game.state.stack.shouldBeEmpty()
                }
                withClue("It stays prepared with its one copy") {
                    game.state.getEntity(prodigy)?.get<PreparedComponent>() shouldNotBe null
                    game.prepareCopies().size shouldBe 1
                }
            }
        }
    }
}
