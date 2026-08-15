package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.PlayLand
import com.wingedsheep.engine.state.components.battlefield.TappedComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Spelunking (LCI #213, {2}{G} Enchantment).
 *
 * "When this enchantment enters, draw a card, then you may put a land card from your hand onto the
 * battlefield. If you put a Cave onto the battlefield this way, you gain 4 life.
 * Lands you control enter untapped."
 *
 * Covers:
 *  1. ETB puts a Cave → +4 life, Cave on battlefield, card drawn.
 *  2. ETB puts a non-Cave land → no life gain.
 *  3. ETB decline ("you may") → land stays in hand, no life gain.
 *  4. Static "Lands you control enter untapped" makes an otherwise-tapland (Hidden Cataract, a Cave
 *     that enters tapped) enter untapped while Spelunking is on the battlefield.
 *  5. Control: without Spelunking, Hidden Cataract enters tapped.
 *
 * Spelunking and the Cave lands are auto-discovered from the LCI set, so no explicit registration.
 */
class SpelunkingScenarioTest : ScenarioTestBase() {

    init {
        context("Spelunking") {

            test("ETB — draw a card, put a Cave from hand, gain 4 life") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Spelunking")
                    .withCardInHand(1, "Captivating Cave")
                    .withLandsOnBattlefield(1, "Forest", 3) // pay {2}{G}
                    .withCardInLibrary(1, "Forest")         // draw target
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val lifeBefore = game.getLifeTotal(1)

                game.castSpell(1, "Spelunking").error shouldBe null
                game.resolveStack()

                withClue("ETB pauses on the 'may put a land' selection") {
                    game.hasPendingDecision() shouldBe true
                }
                withClue("the card was drawn from the library") {
                    game.librarySize(1) shouldBe 0
                }

                val cave = game.findCardsInHand(1, "Captivating Cave").single()
                game.selectCards(listOf(cave))
                game.resolveStack()

                withClue("Captivating Cave was put onto the battlefield") {
                    game.isOnBattlefield("Captivating Cave") shouldBe true
                }
                withClue("gained 4 life for putting a Cave") {
                    game.getLifeTotal(1) shouldBe lifeBefore + 4
                }
            }

            test("ETB — putting a non-Cave land grants no life") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Spelunking")
                    .withCardInHand(1, "Forest")            // the land to put (non-Cave)
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withCardInLibrary(1, "Grizzly Bears")  // draw target (nonland, stays in hand)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val lifeBefore = game.getLifeTotal(1)

                game.castSpell(1, "Spelunking").error shouldBe null
                game.resolveStack()

                val forest = game.findCardsInHand(1, "Forest").single()
                game.selectCards(listOf(forest))
                game.resolveStack()

                withClue("the Forest was put onto the battlefield") {
                    game.isOnBattlefield("Forest") shouldBe true
                }
                withClue("no life gained — a Forest is not a Cave") {
                    game.getLifeTotal(1) shouldBe lifeBefore
                }
            }

            test("ETB — declining the 'may' leaves the land in hand and grants no life") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Spelunking")
                    .withCardInHand(1, "Captivating Cave")
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val lifeBefore = game.getLifeTotal(1)

                game.castSpell(1, "Spelunking").error shouldBe null
                game.resolveStack()

                game.skipSelection()
                game.resolveStack()

                withClue("declined — Cave stays in hand") {
                    game.isInHand(1, "Captivating Cave") shouldBe true
                    game.isOnBattlefield("Captivating Cave") shouldBe false
                }
                withClue("no Cave put → no life gain") {
                    game.getLifeTotal(1) shouldBe lifeBefore
                }
            }

            test("static — lands you control enter untapped (Hidden Cataract enters untapped)") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Spelunking")
                    .withCardInHand(1, "Hidden Cataract") // a Cave that normally enters tapped
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cataract = game.findCardsInHand(1, "Hidden Cataract").single()
                game.execute(PlayLand(game.player1Id, cataract)).error shouldBe null

                val perm = game.findPermanent("Hidden Cataract")!!
                withClue("Hidden Cataract enters untapped thanks to Spelunking's static") {
                    game.state.getEntity(perm)?.has<TappedComponent>() shouldBe false
                }
            }

            test("control — without Spelunking, Hidden Cataract enters tapped") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Hidden Cataract")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val cataract = game.findCardsInHand(1, "Hidden Cataract").single()
                game.execute(PlayLand(game.player1Id, cataract)).error shouldBe null

                val perm = game.findPermanent("Hidden Cataract")!!
                withClue("baseline: Hidden Cataract enters tapped on its own") {
                    game.state.getEntity(perm)?.has<TappedComponent>() shouldBe true
                }
            }
        }
    }
}
