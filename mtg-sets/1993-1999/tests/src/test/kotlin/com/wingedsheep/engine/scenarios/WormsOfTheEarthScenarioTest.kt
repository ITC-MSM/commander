package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario tests for Worms of the Earth (DRK #56).
 *
 * {2}{B}{B}{B} Enchantment
 * "Players can't play lands.
 *  Lands can't enter the battlefield.
 *  At the beginning of each upkeep, any player may sacrifice two lands of their choice or have this
 *  enchantment deal 5 damage to that player. If a player does either, destroy this enchantment."
 *
 * The two lock lines are separate events and neither subsumes the other, so both are tested: a land
 * can't be *played* from hand, and a land can't be *put onto the battlefield* by an effect either.
 */
class WormsOfTheEarthScenarioTest : ScenarioTestBase() {

    init {
        context("Worms of the Earth") {

            test("a land in hand can't be played") {
                val game = scenario()
                    .withPlayers("Wormlord", "Farmer")
                    .withCardOnBattlefield(1, "Worms of the Earth")
                    .withCardInHand(2, "Forest")
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val forest = game.findCardsInHand(2, "Forest").single()
                val result = game.execute(PlayLand(game.player2Id, forest))
                withClue("the lock rejects the land drop") {
                    result.error shouldNotBe null
                }
                withClue("and the Forest is still in hand") {
                    game.findCardsInHand(2, "Forest").size shouldBe 1
                }
            }

            test("a player can buy their way out by sacrificing two lands") {
                val game = scenario()
                    .withPlayers("Wormlord", "Farmer")
                    .withCardOnBattlefield(1, "Worms of the Earth")
                    .withLandsOnBattlefield(1, "Swamp", 3)
                    .withCardInLibrary(1, "Grizzly Bears")
                    .withCardInLibrary(2, "Grizzly Bears")
                    .withActivePlayer(2)
                    .inPhase(Phase.ENDING, Step.END)
                    .build()

                game.passUntilPhase(Phase.BEGINNING, Step.UPKEEP)
                game.state.activePlayerId shouldBe game.player1Id
                game.resolveStack()

                // The active player is offered the escape first (APNAP).
                val offer = game.state.pendingDecision
                offer.shouldNotBeNull()
                offer.shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(true)

                // Then which way out: mode 0 is the two lands.
                val mode = game.state.pendingDecision
                mode.shouldNotBeNull()
                mode.shouldBeInstanceOf<ChooseOptionDecision>()
                game.submitDecision(OptionChosenResponse(mode.id, 0))

                // Which two lands to sacrifice.
                val whichLands = game.state.pendingDecision
                whichLands.shouldNotBeNull()
                whichLands.shouldBeInstanceOf<SelectCardsDecision>()
                game.submitDecision(
                    CardsSelectedResponse(whichLands.id, whichLands.options.take(2))
                )
                game.resolveStack()

                withClue("paying destroys the enchantment") {
                    game.findPermanent("Worms of the Earth").shouldBeNull()
                }
                withClue("and costs two of the three Swamps") {
                    game.findPermanents("Swamp").size shouldBe 1
                }
            }

            test("the enchantment itself is unaffected — it locks lands, not everything") {
                val game = scenario()
                    .withPlayers("Wormlord", "Farmer")
                    .withCardOnBattlefield(1, "Worms of the Earth")
                    .withCardInHand(2, "Grizzly Bears")
                    .withLandsOnBattlefield(2, "Forest", 3)
                    .withActivePlayer(2)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(2, "Grizzly Bears").error shouldBe null
                game.resolveStack()
                withClue("a creature spell is untouched by a land lock") {
                    game.findPermanent("Grizzly Bears").shouldNotBeNull()
                }
            }
        }
    }
}
