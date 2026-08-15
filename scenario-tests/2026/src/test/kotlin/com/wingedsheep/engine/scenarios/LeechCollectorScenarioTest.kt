package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.PreparedComponent
import com.wingedsheep.engine.state.components.battlefield.PreparedSpellCopyComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Leech Collector // Bloodletting (Secrets of Strixhaven).
 *
 * Unlike "enters prepared" preparation creatures, Leech Collector does NOT enter prepared. It only
 * becomes prepared the first time its controller gains life each turn — exercising the new
 * `Effects.BecomePrepared` effect and the `YouGainLifeFirstTimeEachTurn` trigger. Becoming prepared
 * creates a copy of "Bloodletting" ({B}, "Each opponent loses 2 life.") in exile; casting that copy
 * unprepares the creature.
 */
class LeechCollectorScenarioTest : ScenarioTestBase() {

    private fun TestGame.findExileCopy(playerNumber: Int, name: String): com.wingedsheep.sdk.model.EntityId? {
        val playerId = if (playerNumber == 1) player1Id else player2Id
        return state.getExile(playerId).firstOrNull { id ->
            val e = state.getEntity(id)
            e?.get<CardComponent>()?.name == name && e.get<PreparedSpellCopyComponent>() != null
        }
    }

    init {
        context("Leech Collector — becomes prepared on first life gain each turn") {

            test("does not enter prepared") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Leech Collector")
                    .withLandsOnBattlefield(1, "Swamp", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                game.castSpell(1, "Leech Collector")
                game.resolveStack()

                val leech = game.findPermanent("Leech Collector")!!
                withClue("Leech Collector has no PREPARED keyword, so it must NOT enter prepared") {
                    game.state.getEntity(leech)?.get<PreparedComponent>() shouldBe null
                }
                withClue("No prepare-spell copy should exist before any life gain") {
                    game.findExileCopy(1, "Leech Collector") shouldBe null
                }
            }

            test("becomes prepared the first time you gain life, then casting Bloodletting drains and unprepares") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Leech Collector", summoningSickness = false)
                    .withCardInHand(1, "Venerable Monk")
                    // Four Plains so Venerable Monk's {2}{W} is paid entirely from white sources,
                    // leaving the lone Swamp untapped to pay Bloodletting's {B} afterward.
                    .withLandsOnBattlefield(1, "Plains", 4)
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLifeTotal(2, 20)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val leech = game.findPermanent("Leech Collector")!!

                // Cast Venerable Monk — its ETB gains 2 life, the first life gain this turn.
                game.castSpell(1, "Venerable Monk")
                game.resolveStack()

                withClue("Leech Collector should have become prepared on the first life gain") {
                    game.state.getEntity(leech)?.get<PreparedComponent>() shouldNotBe null
                }
                val copyId = game.findExileCopy(1, "Leech Collector")
                withClue("A Bloodletting prepare-spell copy should be in exile") {
                    copyId shouldNotBe null
                }

                // The copy should be castable from exile as face 0 for {B}.
                val prepareAction = game.getLegalActions(1).firstOrNull { la ->
                    val a = la.action
                    a is CastSpell && a.cardId == copyId
                }
                withClue("The Bloodletting copy should be offered as a legal cast from exile") {
                    prepareAction shouldNotBe null
                    (prepareAction!!.action as CastSpell).faceIndex shouldBe 0
                    prepareAction.sourceZone shouldBe "EXILE"
                    prepareAction.manaCostString shouldBe "{B}"
                    prepareAction.isAffordable shouldBe true
                    Unit
                }

                val opponentLifeBefore = game.getLifeTotal(2)
                game.execute(CastSpell(game.player1Id, copyId!!, faceIndex = 0))
                game.resolveStack()

                withClue("The Bloodletting copy should have resolved and left exile") {
                    game.findExileCopy(1, "Leech Collector") shouldBe null
                }
                withClue("Bloodletting makes each opponent lose 2 life") {
                    game.getLifeTotal(2) shouldBe opponentLifeBefore - 2
                }
                withClue("Casting Bloodletting unprepares Leech Collector") {
                    game.state.getEntity(leech)?.get<PreparedComponent>() shouldBe null
                }
                withClue("The Bloodletting copy should be gone from exile") {
                    game.findExileCopy(1, "Leech Collector") shouldBe null
                }
            }

            test("a second life gain the same turn does not re-prepare (only the first time each turn)") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Leech Collector", summoningSickness = false)
                    .withCardInHand(1, "Venerable Monk")
                    .withCardInHand(1, "Venerable Monk")
                    .withLandsOnBattlefield(1, "Plains", 6)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val leech = game.findPermanent("Leech Collector")!!

                game.castSpell(1, "Venerable Monk")
                game.resolveStack()
                val firstCopy = game.findExileCopy(1, "Leech Collector")
                withClue("First life gain prepares Leech Collector") {
                    firstCopy shouldNotBe null
                }

                // Second life gain this turn: trigger condition "for the first time each turn" is no
                // longer met, so no second copy is created (and the existing one stays).
                game.castSpell(1, "Venerable Monk")
                game.resolveStack()

                val copies = game.state.getExile(game.player1Id).filter { id ->
                    val e = game.state.getEntity(id)
                    e?.get<CardComponent>()?.name == "Leech Collector" && e.get<PreparedSpellCopyComponent>() != null
                }
                withClue("A second same-turn life gain must not create a second Bloodletting copy") {
                    copies.size shouldBe 1
                }
                withClue("Leech Collector remains prepared (it never unprepared)") {
                    game.state.getEntity(leech)?.get<PreparedComponent>() shouldNotBe null
                }
            }
        }
    }
}
