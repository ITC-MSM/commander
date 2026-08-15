package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.PreparedComponent
import com.wingedsheep.engine.state.components.battlefield.PreparedSpellCopyComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Skycoach Conductor // All Aboard (Secrets of Strixhaven #67).
 *
 * "{2}{U} Creature — Bird Pilot 2/3. Flash. Flying, vigilance. This creature enters prepared.
 *  // All Aboard — {U} Instant: Exile target non-Pilot creature you control, then return that card
 *  to the battlefield under its owner's control."
 *
 * Exercises the PREPARED enters-prepared keyword + the prepare spell's self-blink. The blink routes
 * the target through exile (a new object, CR 400.7) and returns it under its owner's control;
 * casting the copy unprepares the Conductor.
 */
class SkycoachConductorScenarioTest : ScenarioTestBase() {

    private fun TestGame.findExileCopy(playerNumber: Int, name: String): com.wingedsheep.sdk.model.EntityId? {
        val playerId = if (playerNumber == 1) player1Id else player2Id
        return state.getExile(playerId).firstOrNull { id ->
            val e = state.getEntity(id)
            e?.get<CardComponent>()?.name == name && e.get<PreparedSpellCopyComponent>() != null
        }
    }

    init {
        context("Skycoach Conductor // All Aboard") {

            test("enters prepared with a prepare-spell copy in exile") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Skycoach Conductor")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Skycoach Conductor")
                game.resolveStack()

                val conductor = game.findPermanent("Skycoach Conductor")!!
                withClue("Skycoach Conductor enters prepared") {
                    game.state.getEntity(conductor)?.get<PreparedComponent>() shouldNotBe null
                }
                withClue("A prepare-spell copy ('All Aboard') exists in exile") {
                    game.findExileCopy(1, "Skycoach Conductor") shouldNotBe null
                }
            }

            test("casting All Aboard blinks a non-Pilot creature and unprepares the Conductor") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Skycoach Conductor")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withCardOnBattlefield(1, "Grizzly Bears", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Skycoach Conductor")
                game.resolveStack()

                val conductor = game.findPermanent("Skycoach Conductor")!!
                val bearsBefore = game.findPermanent("Grizzly Bears")!!
                val copyId = game.findExileCopy(1, "Skycoach Conductor")!!

                // Cast the prepare spell (face 0) targeting Grizzly Bears (non-Pilot, you control).
                val cast = game.execute(
                    CastSpell(
                        game.player1Id,
                        copyId,
                        targets = listOf(ChosenTarget.Permanent(bearsBefore)),
                        faceIndex = 0
                    )
                )
                withClue("Casting All Aboard should succeed: ${cast.error}") { cast.error shouldBe null }
                game.resolveStack()

                val inExile = game.state.getExile(game.player1Id).any {
                    game.state.getEntity(it)?.get<CardComponent>()?.name == "Grizzly Bears"
                }
                withClue("Grizzly Bears was blinked and is back on the battlefield, not stranded in exile (inExile=$inExile)") {
                    inExile shouldBe false
                    game.isOnBattlefield("Grizzly Bears") shouldBe true
                }
                withClue("Skycoach Conductor is no longer prepared after casting the copy") {
                    game.state.getEntity(conductor)?.get<PreparedComponent>() shouldBe null
                }
                withClue("The prepare-spell copy is gone from exile") {
                    game.findExileCopy(1, "Skycoach Conductor") shouldBe null
                }
            }

            test("cannot blink a Pilot creature (the Conductor itself is an illegal target)") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Skycoach Conductor")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Skycoach Conductor")
                game.resolveStack()

                val conductor = game.findPermanent("Skycoach Conductor")!!
                val copyId = game.findExileCopy(1, "Skycoach Conductor")!!

                // Skycoach Conductor is a Bird Pilot, so the "non-Pilot" filter must reject it.
                val cast = game.execute(
                    CastSpell(
                        game.player1Id,
                        copyId,
                        targets = listOf(ChosenTarget.Permanent(conductor)),
                        faceIndex = 0
                    )
                )
                withClue("Targeting the Pilot Conductor should be illegal") {
                    cast.error shouldNotBe null
                }
            }
        }
    }
}
