package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.PreparedComponent
import com.wingedsheep.engine.state.components.battlefield.PreparedSpellCopyComponent
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Heartwood Crafter // Soul Tether (Reality Fracture).
 *
 * Its {C} carries `ManaRestriction.CannotCastSpellsFromHand`: a spell cast from hand can't use it,
 * but Soul Tether's prepare-spell copy — cast from exile — can.
 */
class HeartwoodCrafterScenarioTest : ScenarioTestBase() {

    private fun TestGame.prepareCopies(): List<EntityId> =
        state.getExile(player1Id).filter { id ->
            val e = state.getEntity(id)
            e?.get<CardComponent>()?.name == "Heartwood Crafter" && e.get<PreparedSpellCopyComponent>() != null
        }

    init {
        context("Heartwood Crafter") {

            test("its {C} can't pay for a spell cast from hand") {
                var b = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Heartwood Crafter")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withCardInHand(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(5) { b = b.withCardInLibrary(1, "Forest") }
                repeat(5) { b = b.withCardInLibrary(2, "Forest") }
                val game = b.build()

                withClue("Forest + the Crafter's restricted {C} can't pay Grizzly Bears' {1}{G} from hand") {
                    game.castSpell(1, "Grizzly Bears").error shouldNotBe null
                }
                game.isInHand(1, "Grizzly Bears") shouldBe true
            }

            test("it enters prepared, and its {C} pays for the Soul Tether copy cast from exile") {
                var b = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Heartwood Crafter")
                    .withLandsOnBattlefield(1, "Forest", 1)
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(8) { b = b.withCardInLibrary(1, "Island") }
                repeat(8) { b = b.withCardInLibrary(2, "Island") }
                val game = b.build()

                game.castSpell(1, "Heartwood Crafter").error shouldBe null
                game.resolveStack()
                val crafter = game.findPermanent("Heartwood Crafter")!!
                withClue("Heartwood Crafter enters prepared") {
                    game.state.getEntity(crafter)?.get<PreparedComponent>() shouldNotBe null
                    game.prepareCopies().size shouldBe 1
                }

                // To our next main phase, when the Crafter is no longer summoning sick.
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.state.activePlayerId shouldBe game.player2Id
                game.passUntilPhase(Phase.ENDING, Step.END)
                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.state.activePlayerId shouldBe game.player1Id
                game.passUntilPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)

                // Soul Tether costs {2}{R/G}: Forest + Mountain + the Crafter's {C} is exactly enough.
                val copyId = game.prepareCopies().single()
                game.execute(CastSpell(game.player1Id, copyId, emptyList(), faceIndex = 0)).error shouldBe null
                game.resolveStack()

                withClue("The Crafter tapped for the copy") {
                    game.state.getEntity(crafter)?.get<TappedComponent>() shouldNotBe null
                }
                withClue("Soul Tether created a Heartwood token") {
                    game.findPermanent("Heartwood") shouldNotBe null
                }
                withClue("Casting the copy unprepared the Crafter") {
                    game.state.getEntity(crafter)?.get<PreparedComponent>() shouldBe null
                }
            }
        }
    }
}
