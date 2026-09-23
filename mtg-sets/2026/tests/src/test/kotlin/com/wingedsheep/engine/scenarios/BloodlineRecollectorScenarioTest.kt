package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.PreparedComponent
import com.wingedsheep.engine.state.components.battlefield.PreparedSpellCopyComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Bloodline Recollector // Ancestral Craving (Reality Fracture).
 *
 * The end-step trigger counts creatures that died this turn across *all* players (tokens and the
 * caster's own creatures included), fires on every player's end step, and is an intervening-if:
 * with fewer than three deaths it does nothing. Once prepared, the exiled copy of Ancestral Craving
 * ({B} instant) makes its target player draw three and lose 3 life, and casting it unprepares the
 * creature.
 */
class BloodlineRecollectorScenarioTest : ScenarioTestBase() {

    private fun TestGame.findPrepareCopy(playerNumber: Int): EntityId? {
        val playerId = if (playerNumber == 1) player1Id else player2Id
        return state.getExile(playerId).firstOrNull { id ->
            val e = state.getEntity(id)
            e?.get<CardComponent>()?.name == "Bloodline Recollector" && e.get<PreparedSpellCopyComponent>() != null
        }
    }

    private fun TestGame.shock(casterNumber: Int, targetName: String, controllerNumber: Int) {
        val controllerId = if (controllerNumber == 1) player1Id else player2Id
        val target = state.getBattlefield().first { id ->
            state.getEntity(id)?.get<CardComponent>()?.name == targetName &&
                state.projectedState.getController(id) == controllerId
        }
        castSpell(casterNumber, "Shock", target).error shouldBe null
        resolveStack()
    }

    private fun builder(activePlayer: Int, shocks: Int): ScenarioBuilder {
        val caster = activePlayer
        var b = scenario()
            .withPlayers("Player", "Opponent")
            .withCardOnBattlefield(1, "Bloodline Recollector")
            .withCardOnBattlefield(1, "Grizzly Bears")
            .withCardOnBattlefield(2, "Llanowar Elves")
            .withCardOnBattlefield(2, "Savannah Lions")
            .withLandsOnBattlefield(1, "Swamp", 1)
            .withLandsOnBattlefield(caster, "Mountain", shocks)
            .withActivePlayer(activePlayer)
            .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
        repeat(shocks) { b = b.withCardInHand(caster, "Shock") }
        repeat(6) { b = b.withCardInLibrary(1, "Swamp") }
        repeat(6) { b = b.withCardInLibrary(2, "Forest") }
        return b
    }

    init {
        context("Bloodline Recollector — end-step prepare") {

            test("three deaths across both players prepare it; Ancestral Craving draws three, drains 3, unprepares") {
                val game = builder(activePlayer = 1, shocks = 3).build()
                val recollector = game.findPermanent("Bloodline Recollector")!!

                // One of the three deaths is the caster's own creature — the count is global.
                game.shock(1, "Llanowar Elves", controllerNumber = 2)
                game.shock(1, "Savannah Lions", controllerNumber = 2)
                game.shock(1, "Grizzly Bears", controllerNumber = 1)

                withClue("The trigger waits for the end step") {
                    game.state.getEntity(recollector)?.get<PreparedComponent>() shouldBe null
                }

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("Three creatures died this turn, so it becomes prepared") {
                    game.state.getEntity(recollector)?.get<PreparedComponent>() shouldNotBe null
                }
                val copyId = game.findPrepareCopy(1)
                withClue("An Ancestral Craving copy sits in exile") { copyId shouldNotBe null }

                val cast = game.getLegalActions(1).firstOrNull { la ->
                    (la.action as? CastSpell)?.cardId == copyId
                }
                withClue("The copy is castable at instant speed for {B} as face 0") {
                    cast shouldNotBe null
                    (cast!!.action as CastSpell).faceIndex shouldBe 0
                    cast.manaCostString shouldBe "{B}"
                    cast.isAffordable shouldBe true
                }

                val handBefore = game.handSize(2)
                val lifeBefore = game.getLifeTotal(2)
                game.execute(
                    CastSpell(game.player1Id, copyId!!, listOf(ChosenTarget.Player(game.player2Id)), faceIndex = 0)
                ).error shouldBe null
                game.resolveStack()

                withClue("Target player draws three cards") { game.handSize(2) shouldBe handBefore + 3 }
                withClue("Target player loses 3 life") { game.getLifeTotal(2) shouldBe lifeBefore - 3 }
                withClue("Casting the copy unprepares the creature") {
                    game.state.getEntity(recollector)?.get<PreparedComponent>() shouldBe null
                }
                withClue("The copy has left exile") { game.findPrepareCopy(1) shouldBe null }
            }

            test("only two deaths — the intervening-if fails and it stays unprepared") {
                val game = builder(activePlayer = 1, shocks = 2).build()
                val recollector = game.findPermanent("Bloodline Recollector")!!

                game.shock(1, "Llanowar Elves", controllerNumber = 2)
                game.shock(1, "Savannah Lions", controllerNumber = 2)

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                game.state.getEntity(recollector)?.get<PreparedComponent>() shouldBe null
                game.findPrepareCopy(1) shouldBe null
            }

            test("it also triggers on an opponent's end step") {
                val game = builder(activePlayer = 2, shocks = 3).build()
                val recollector = game.findPermanent("Bloodline Recollector")!!

                game.shock(2, "Llanowar Elves", controllerNumber = 2)
                game.shock(2, "Savannah Lions", controllerNumber = 2)
                game.shock(2, "Grizzly Bears", controllerNumber = 1)

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                game.state.getEntity(recollector)?.get<PreparedComponent>() shouldNotBe null
                game.findPrepareCopy(1) shouldNotBe null
            }
        }
    }
}
