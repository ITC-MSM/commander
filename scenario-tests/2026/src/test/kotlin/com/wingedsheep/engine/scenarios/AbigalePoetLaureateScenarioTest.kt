package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.state.components.battlefield.PreparedComponent
import com.wingedsheep.engine.state.components.battlefield.PreparedSpellCopyComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Abigale, Poet Laureate // Heroic Stanza (Secrets of Strixhaven).
 *
 * Abigale does NOT enter prepared (no PREPARED keyword). She becomes prepared via her trigger —
 * whenever you cast a creature spell — through `Effects.BecomePrepared` + the `YouCastCreature`
 * trigger. Becoming prepared creates a copy of "Heroic Stanza" ({1}{W/B}, "Put a +1/+1 counter on
 * target creature.") in exile; casting that copy unprepares her.
 */
class AbigalePoetLaureateScenarioTest : ScenarioTestBase() {

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
        context("Abigale — becomes prepared when you cast a creature spell") {

            test("does not enter prepared") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Abigale, Poet Laureate")
                    .withLandsOnBattlefield(1, "Plains", 2)
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                game.castSpell(1, "Abigale, Poet Laureate")
                game.resolveStack()

                val abigale = game.findPermanent("Abigale, Poet Laureate")!!
                withClue("Abigale has no PREPARED keyword, so she must NOT enter prepared") {
                    game.state.getEntity(abigale)?.get<PreparedComponent>() shouldBe null
                }
                withClue("No prepare-spell copy should exist before any creature spell is cast") {
                    game.findExileCopy(1, "Abigale, Poet Laureate") shouldBe null
                }
            }

            test("becomes prepared when you cast a creature spell, then casting Heroic Stanza adds a +1/+1 counter and unprepares") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Abigale, Poet Laureate", summoningSickness = false)
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Forest", 2)
                    .withLandsOnBattlefield(1, "Plains", 1)
                    .withLandsOnBattlefield(1, "Swamp", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val abigale = game.findPermanent("Abigale, Poet Laureate")!!

                // Cast a creature spell — triggers "Abigale becomes prepared".
                game.castSpell(1, "Grizzly Bears")
                game.resolveStack()

                withClue("Casting a creature spell should make Abigale prepared") {
                    game.state.getEntity(abigale)?.get<PreparedComponent>() shouldNotBe null
                }
                val copyId = game.findExileCopy(1, "Abigale, Poet Laureate")
                withClue("A Heroic Stanza prepare-spell copy should be in exile") {
                    copyId shouldNotBe null
                }

                // The copy should be castable from exile as face 0.
                val prepareAction = game.getLegalActions(1).firstOrNull { la ->
                    val a = la.action
                    a is CastSpell && a.cardId == copyId
                }
                withClue("The Heroic Stanza copy should be offered as a legal cast from exile") {
                    prepareAction shouldNotBe null
                    (prepareAction!!.action as CastSpell).faceIndex shouldBe 0
                    prepareAction.sourceZone shouldBe "EXILE"
                    Unit
                }

                // Cast Heroic Stanza targeting the Grizzly Bears.
                val bears = game.findPermanent("Grizzly Bears")!!
                game.execute(
                    CastSpell(
                        game.player1Id,
                        copyId!!,
                        targets = listOf(ChosenTarget.Permanent(bears)),
                        faceIndex = 0,
                    )
                )
                game.resolveStack()

                withClue("Heroic Stanza should put one +1/+1 counter on Grizzly Bears") {
                    game.plusOneCounters(bears) shouldBe 1
                }
                withClue("Casting Heroic Stanza unprepares Abigale") {
                    game.state.getEntity(abigale)?.get<PreparedComponent>() shouldBe null
                }
                withClue("The Heroic Stanza copy should be gone from exile") {
                    game.findExileCopy(1, "Abigale, Poet Laureate") shouldBe null
                }
            }

            test("casting a noncreature spell does not prepare Abigale") {
                var builder = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Abigale, Poet Laureate", summoningSickness = false)
                    .withCardInHand(1, "Lightning Bolt")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(5) { builder = builder.withCardInLibrary(2, "Forest") }
                val game = builder.build()

                val abigale = game.findPermanent("Abigale, Poet Laureate")!!

                game.castSpellTargetingPlayer(1, "Lightning Bolt", 2)
                game.resolveStack()

                withClue("A noncreature spell must not prepare Abigale") {
                    game.state.getEntity(abigale)?.get<PreparedComponent>() shouldBe null
                    game.findExileCopy(1, "Abigale, Poet Laureate") shouldBe null
                }
            }
        }
    }
}
