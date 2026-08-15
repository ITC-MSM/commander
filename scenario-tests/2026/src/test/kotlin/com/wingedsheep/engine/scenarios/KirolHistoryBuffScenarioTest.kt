package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.PreparedComponent
import com.wingedsheep.engine.state.components.battlefield.PreparedSpellCopyComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Kirol, History Buff // Pack a Punch (Secrets of Strixhaven).
 *
 * Kirol does NOT enter prepared (no PREPARED keyword). He becomes prepared via the batched
 * "Whenever one or more cards leave your graveyard" trigger (`Triggers.CardsLeaveYourGraveyard`)
 * + `Effects.BecomePrepared`. Becoming prepared creates a copy of "Pack a Punch" ({1}{R}{W}, "Mill
 * a card. Put two +1/+1 counters on target creature. It gains trample until end of turn.") in exile;
 * casting that copy unprepares him.
 */
class KirolHistoryBuffScenarioTest : ScenarioTestBase() {

    private fun TestGame.findExileCopy(playerNumber: Int, name: String): com.wingedsheep.sdk.model.EntityId? {
        val playerId = if (playerNumber == 1) player1Id else player2Id
        return state.getExile(playerId).firstOrNull { id ->
            val e = state.getEntity(id)
            e?.get<CardComponent>()?.name == name && e.get<PreparedSpellCopyComponent>() != null
        }
    }

    private fun TestGame.plusOneCounters(id: com.wingedsheep.sdk.model.EntityId): Int =
        state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    init {
        context("Kirol — becomes prepared when one or more cards leave your graveyard") {

            test("does not enter prepared") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Kirol, History Buff")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                game.castSpell(1, "Kirol, History Buff")
                game.resolveStack()

                val kirol = game.findPermanent("Kirol, History Buff")!!
                withClue("Kirol has no PREPARED keyword, so he must NOT enter prepared") {
                    game.state.getEntity(kirol)?.get<PreparedComponent>() shouldBe null
                }
                withClue("No prepare-spell copy should exist before anything leaves the graveyard") {
                    game.findExileCopy(1, "Kirol, History Buff") shouldBe null
                }
            }

            test("becomes prepared when a card leaves your graveyard, then casting Pack a Punch mills, buffs, and unprepares") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Kirol, History Buff", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withCardInHand(1, "Raise Dead")
                    .withCardInGraveyard(1, "Glory Seeker")
                    // {B} for Raise Dead, then {1}{R}{W} for Pack a Punch.
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val kirol = game.findPermanent("Kirol, History Buff")!!
                val bears = game.findPermanent("Grizzly Bears")!!
                val librarySizeBefore = game.state.getLibrary(game.player1Id).size

                // Cast Raise Dead returning Glory Seeker — Glory Seeker leaves the graveyard → triggers Kirol.
                val glorySeeker = game.findCardsInGraveyard(1, "Glory Seeker").first()
                game.castSpellTargetingGraveyardCard(1, "Raise Dead", listOf(glorySeeker))
                game.resolveStack()

                withClue("A card leaving the graveyard should make Kirol prepared") {
                    game.state.getEntity(kirol)?.get<PreparedComponent>() shouldNotBe null
                }
                val copyId = game.findExileCopy(1, "Kirol, History Buff")
                withClue("A Pack a Punch prepare-spell copy should be in exile") {
                    copyId shouldNotBe null
                }

                // Cast Pack a Punch targeting the Grizzly Bears.
                game.execute(
                    CastSpell(
                        game.player1Id,
                        copyId!!,
                        targets = listOf(ChosenTarget.Permanent(bears)),
                        faceIndex = 0,
                    )
                )
                game.resolveStack()

                withClue("Pack a Punch mills one card (library shrinks by 1)") {
                    game.state.getLibrary(game.player1Id).size shouldBe librarySizeBefore - 1
                }
                withClue("Pack a Punch puts two +1/+1 counters on Grizzly Bears") {
                    game.plusOneCounters(bears) shouldBe 2
                }
                withClue("Grizzly Bears gains trample until end of turn") {
                    game.state.projectedState.hasKeyword(bears, Keyword.TRAMPLE) shouldBe true
                }
                withClue("Casting Pack a Punch unprepares Kirol") {
                    game.state.getEntity(kirol)?.get<PreparedComponent>() shouldBe null
                }
                withClue("The Pack a Punch copy should be gone from exile") {
                    game.findExileCopy(1, "Kirol, History Buff") shouldBe null
                }
            }
        }
    }
}
