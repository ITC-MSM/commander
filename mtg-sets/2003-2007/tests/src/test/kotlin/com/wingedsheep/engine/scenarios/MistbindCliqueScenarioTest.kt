package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Mistbind Clique (LRW #75) — flash, flying, champion a Faerie, and the CR 702.72c payoff:
 * "When a Faerie is championed with this creature, tap all lands target player controls."
 *
 * The champion keyword's own matrix lives in `ChampionKeywordTest`. What this file pins is the
 * follow-up trigger: that it fires *only* when a Faerie was actually championed, that it is a
 * separate targeted ability (so the player is chosen on the stack), and that it taps only that
 * player's lands.
 */
class MistbindCliqueScenarioTest : ScenarioTestBase() {

    private val stateProjector = StateProjector()

    private fun tappedCount(game: ScenarioTestBase.TestGame, ids: List<EntityId>): Int =
        ids.count { game.state.getEntity(it)?.has<TappedComponent>() == true }

    init {
        context("Mistbind Clique — when a Faerie is championed with this creature") {

            test("championing a Faerie taps all lands the targeted player controls") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Mistbind Clique")
                    .withCardOnBattlefield(1, "Spellstutter Sprite")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withLandsOnBattlefield(2, "Forest", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bobLands = game.findPermanents("Forest")
                val sprite = game.findPermanent("Spellstutter Sprite")!!

                game.castSpell(1, "Mistbind Clique").error shouldBe null
                game.resolveStack()
                game.selectCards(listOf(sprite)).error shouldBe null
                game.resolveStack()

                withClue("the champion clause exiled the Faerie, so the Clique stays") {
                    game.isOnBattlefield("Mistbind Clique") shouldBe true
                    game.isInExile(1, "Spellstutter Sprite") shouldBe true
                }

                // Alice's own Islands are already tapped — she paid for the Clique with them — so
                // the honest assertion is that the trigger changes nothing on her side.
                val aliceTappedBefore = tappedCount(game, game.findPermanents("Island"))
                withClue("nothing has tapped Bob's lands yet") {
                    tappedCount(game, bobLands) shouldBe 0
                }

                // The follow-up trigger is a separate object on the stack with its own player
                // target, chosen when it is put on the stack rather than when the Clique entered.
                game.selectTargets(listOf(game.player2Id)).error shouldBe null
                game.resolveStack()

                withClue("all three of Bob's lands are tapped") {
                    tappedCount(game, bobLands) shouldBe 3
                }
                withClue("Alice's own lands are untouched — only the targeted player's tap") {
                    tappedCount(game, game.findPermanents("Island")) shouldBe aliceTappedBefore
                }
            }

            test("declining the champion choice sacrifices the Clique and taps nothing") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardInHand(1, "Mistbind Clique")
                    .withCardOnBattlefield(1, "Spellstutter Sprite")
                    .withLandsOnBattlefield(1, "Island", 4)
                    .withLandsOnBattlefield(2, "Forest", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Mistbind Clique").error shouldBe null
                game.resolveStack()
                game.skipSelection().error shouldBe null
                game.resolveStack()

                game.isInGraveyard(1, "Mistbind Clique") shouldBe true
                withClue("nothing was championed, so the follow-up never triggered") {
                    game.hasPendingDecision() shouldBe false
                    tappedCount(game, game.findPermanents("Forest")) shouldBe 0
                }
            }

            test("it is a 4/4 with flying") {
                val game = scenario()
                    .withPlayers("Alice", "Bob")
                    .withCardOnBattlefield(1, "Mistbind Clique")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val clique = game.findPermanent("Mistbind Clique")!!
                val projected = stateProjector.project(game.state)
                projected.hasKeyword(clique, Keyword.FLYING) shouldBe true
                projected.getPower(clique) shouldBe 4
                projected.getToughness(clique) shouldBe 4
            }
        }
    }
}
