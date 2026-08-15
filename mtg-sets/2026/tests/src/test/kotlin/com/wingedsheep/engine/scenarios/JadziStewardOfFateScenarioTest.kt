package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.PreparedComponent
import com.wingedsheep.engine.state.components.battlefield.PreparedSpellCopyComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Jadzi, Steward of Fate // Oracle's Gift (Secrets of Strixhaven #55).
 *
 * Jadzi ({2}{U} Legendary Creature, 2/4) enters prepared and, on ETB, draws two then discards
 * two. Her prepare spell Oracle's Gift ({X}{X}{U}, Sorcery) creates X 0/0 Fractals and then puts
 * X +1/+1 counters on each Fractal you control.
 *
 * Exercises the prepare-on-ETB seam and the X-token / X-counters-on-each-Fractal recipe.
 */
class JadziStewardOfFateScenarioTest : ScenarioTestBase() {

    private fun TestGame.findExileCopy(name: String): EntityId? =
        state.getExile(player1Id).firstOrNull { e ->
            val ent = state.getEntity(e)
            ent?.get<CardComponent>()?.name == name && ent.get<PreparedSpellCopyComponent>() != null
        }

    private fun plusOneCounters(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    init {
        context("Jadzi, Steward of Fate") {

            test("Jadzi enters prepared, and casting Oracle's Gift for X=2 makes two 2/2 Fractals") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Jadzi, Steward of Fate")
                    .withCardInHand(1, "Grizzly Bears")
                    .withCardInHand(1, "Mountain")
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Island")
                    .withLandsOnBattlefield(1, "Island", 12)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Jadzi, Steward of Fate").error shouldBe null
                game.resolveStack()
                // ETB draws two then discards two; resolve any pending discard selection.
                while (game.state.pendingDecision != null) {
                    val hand = game.findCardsInHand(1, "Grizzly Bears") + game.findCardsInHand(1, "Mountain")
                    game.selectCards(hand.take(2))
                }

                val jadzi = game.findPermanent("Jadzi, Steward of Fate")!!
                withClue("Jadzi should be prepared on ETB") {
                    game.state.getEntity(jadzi)?.get<PreparedComponent>() shouldNotBe null
                }

                val copyId = game.findExileCopy("Jadzi, Steward of Fate")
                withClue("A prepare-spell copy (Oracle's Gift) should be in exile") {
                    copyId shouldNotBe null
                }

                // The cast-from-exile enumerator surfaces the prepare-spell copy; read its face index
                // so the cast matches what the engine offers (same discovery as PrepareMechanic test).
                val prepareAction = game.getLegalActions(1).firstOrNull { la ->
                    val a = la.action
                    a is CastSpell && a.cardId == copyId
                }
                withClue("The prepare-spell copy (Oracle's Gift) should be offered as a legal cast from exile") {
                    prepareAction shouldNotBe null
                }
                val faceIndex = (prepareAction!!.action as CastSpell).faceIndex

                // Cast Oracle's Gift for X = 2.
                val cast = game.execute(
                    CastSpell(game.player1Id, copyId!!, targets = emptyList(), faceIndex = faceIndex, xValue = 2),
                )
                withClue("Casting Oracle's Gift should succeed: ${cast.error}") {
                    cast.error shouldBe null
                }
                game.resolveStack()

                val fractals = game.findAllPermanents("Fractal Token")
                withClue("Oracle's Gift with X=2 creates two Fractal tokens") {
                    fractals.size shouldBe 2
                }
                withClue("Each Fractal gets two +1/+1 counters (0/0 base → 2/2)") {
                    fractals.forEach { plusOneCounters(game, it) shouldBe 2 }
                }
                withClue("Casting the prepare copy unprepares Jadzi") {
                    game.state.getEntity(jadzi)?.get<PreparedComponent>() shouldBe null
                }
            }
        }
    }
}
