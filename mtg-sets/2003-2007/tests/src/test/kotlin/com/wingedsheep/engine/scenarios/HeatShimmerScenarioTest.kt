package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Heat Shimmer (LRW #175) — "Create a token that's a copy of target creature, except it has haste
 * and 'At the beginning of the end step, exile this token.'"
 *
 * Electroduplicate's exile sibling. Two things separate it from that card and are worth proving:
 * the token leaves via **exile**, not the graveyard (so it triggers nothing on the way out), and
 * the copy may be made of a creature you don't control.
 */
class HeatShimmerScenarioTest : ScenarioTestBase() {

    init {
        context("Heat Shimmer") {

            test("copies an opponent's creature, and the copy has haste") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Heat Shimmer")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withCardOnBattlefield(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Heat Shimmer", bears).error shouldBe null
                game.resolveStack()

                val copies = game.findPermanents("Grizzly Bears")
                withClue("The original plus one token copy") {
                    copies.size shouldBe 2
                }
                val token = copies.first { it != bears }
                withClue("The token is under Heat Shimmer's controller, not the copied creature's") {
                    game.state.projectedState.getController(token) shouldBe game.player1Id
                }
                withClue("The \"except it has haste\" copy exception landed") {
                    game.state.projectedState.hasKeyword(token, Keyword.HASTE) shouldBe true
                }
            }

            test("the token is exiled at the next end step, not sacrificed") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Heat Shimmer")
                    .withLandsOnBattlefield(1, "Mountain", 3)
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                game.castSpell(1, "Heat Shimmer", bears).error shouldBe null
                game.resolveStack()
                game.findPermanents("Grizzly Bears").size shouldBe 2

                game.passUntilPhase(Phase.ENDING, Step.END)
                game.resolveStack()

                withClue("Only the original Bears is left on the battlefield") {
                    val remaining = game.findPermanents("Grizzly Bears")
                    remaining.size shouldBe 1
                    remaining.single() shouldBe bears
                }
                withClue("A token that is exiled never reaches a graveyard, so nothing died") {
                    game.state.getGraveyard(game.player1Id).none {
                        game.state.getEntity(it)?.get<CardComponent>()?.name == "Grizzly Bears"
                    } shouldBe true
                }
                withClue("The original creature is untouched by its copy's expiry") {
                    game.findPermanent("Grizzly Bears") shouldNotBe null
                }
            }
        }
    }
}
