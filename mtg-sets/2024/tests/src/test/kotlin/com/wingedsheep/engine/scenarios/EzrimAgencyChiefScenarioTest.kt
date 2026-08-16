package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Ezrim, Agency Chief (MKM #202) — {1}{W}{W}{U}{U} 5/5 Legendary Archon Detective.
 *
 * "Flying
 *  When Ezrim enters, investigate twice.
 *  {1}, Sacrifice an artifact: Ezrim gains your choice of vigilance, lifelink, or hexproof until
 *  end of turn."
 *
 * The enters trigger banks the fodder the activated ability burns, so the two halves are tested
 * together: enter, then eat one of the Clues you just made.
 *
 * The mode tests each pick a *different* branch, because a modal effect whose executor ignored the
 * chosen index would still pass a single-mode test. Selecting "lifelink" and finding vigilance
 * would be a silent index bug; selecting each in turn makes that impossible to miss.
 */
class EzrimAgencyChiefScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    private val ezrimAbility by lazy {
        cardRegistry.getCard("Ezrim, Agency Chief")!!.script.activatedAbilities.single().id
    }

    init {
        /** Ezrim on the battlefield with two Clues banked by its own enters trigger. */
        fun ezrimWithClues(): TestGame {
            val game = scenario()
                .withPlayers("Chief", "Opponent")
                .withCardInHand(1, "Ezrim, Agency Chief")
                // Five for Ezrim, plus headroom for two {1} activations.
                .withLandsOnBattlefield(1, "Plains", 4)
                .withLandsOnBattlefield(1, "Island", 4)
                .withCardInLibrary(1, "Grizzly Bears")
                .withActivePlayer(1)
                .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                .build()
            val cast = game.castSpell(1, "Ezrim, Agency Chief")
            withClue("casting should succeed: ${cast.error}") { cast.error shouldBe null }
            game.resolveStack()
            return game
        }

        /** Activate, sacrificing [artifact], and pick mode [modeIndex] (0 vig, 1 lifelink, 2 hexproof). */
        fun activateChoosing(game: TestGame, ezrim: EntityId, artifact: EntityId, modeIndex: Int) {
            val result = game.execute(
                ActivateAbility(
                    playerId = game.player1Id,
                    sourceId = ezrim,
                    abilityId = ezrimAbility,
                    costPayment = AdditionalCostPayment(sacrificedPermanents = listOf(artifact))
                )
            )
            withClue("activating should succeed: ${result.error}") { result.error shouldBe null }
            game.resolveStack()
            val modeDecision = game.getPendingDecision() as ChooseOptionDecision
            game.submitDecision(OptionChosenResponse(modeDecision.id, modeIndex))
            game.resolveStack()
        }

        context("Ezrim, Agency Chief") {

            test("entering investigates twice") {
                val game = ezrimWithClues()

                withClue("two Clues, not one — 'investigate twice' is two tokens") {
                    game.findPermanents("Clue").size shouldBe 2
                }
                withClue("and Ezrim itself flies") {
                    val ezrim = game.findPermanent("Ezrim, Agency Chief")!!
                    stateProjector.project(game.state).hasKeyword(ezrim, Keyword.FLYING) shouldBe true
                }
            }

            test("sacrificing a Clue for vigilance grants vigilance and nothing else") {
                val game = ezrimWithClues()
                val ezrim = game.findPermanent("Ezrim, Agency Chief")!!
                val clue = game.findPermanents("Clue").first()

                activateChoosing(game, ezrim, clue, modeIndex = 0)

                val projected = stateProjector.project(game.state)
                withClue("mode 0 is vigilance") {
                    projected.hasKeyword(ezrim, Keyword.VIGILANCE) shouldBe true
                }
                withClue("the other two modes were not chosen, so they were not granted") {
                    projected.hasKeyword(ezrim, Keyword.LIFELINK) shouldBe false
                    projected.hasKeyword(ezrim, Keyword.HEXPROOF) shouldBe false
                }
                withClue("one Clue was spent as the cost, one remains") {
                    game.findPermanents("Clue").size shouldBe 1
                }
            }

            test("a second activation picks a different mode") {
                val game = ezrimWithClues()
                val ezrim = game.findPermanent("Ezrim, Agency Chief")!!
                val clues = game.findPermanents("Clue")

                activateChoosing(game, ezrim, clues[0], modeIndex = 1)
                activateChoosing(game, ezrim, clues[1], modeIndex = 2)

                val projected = stateProjector.project(game.state)
                withClue("both grants stack — the ability isn't a mutually exclusive switch") {
                    projected.hasKeyword(ezrim, Keyword.LIFELINK) shouldBe true
                    projected.hasKeyword(ezrim, Keyword.HEXPROOF) shouldBe true
                }
                withClue("vigilance was never chosen") {
                    projected.hasKeyword(ezrim, Keyword.VIGILANCE) shouldBe false
                }
                withClue("both Clues are gone") {
                    game.findPermanents("Clue").size shouldBe 0
                }
            }
        }
    }
}
