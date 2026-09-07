package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Blood Funnel (RAV #77) — a {2} discount on noncreature spells, paid for in creatures: every
 * noncreature spell you cast is countered unless you sacrifice one.
 *
 * The discount is proved the only honest way — by casting a spell the player could not otherwise
 * afford — and the tax is proved on all three of its branches: pay, decline, and nothing to pay
 * with (where the punisher must not raise a pointless prompt).
 */
class BloodFunnelScenarioTest : ScenarioTestBase() {

    init {
        context("Blood Funnel") {

            test("a noncreature spell you could not otherwise afford becomes castable") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Blood Funnel")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Inspiration") // {3}{U}
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("{3}{U} reduced by {2} is {1}{U} — two Islands cover it") {
                    game.castSpellTargetingPlayer(1, "Inspiration", 1).error shouldBe null
                }
            }

            test("without the Funnel the same two lands can't pay for it") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Inspiration")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingPlayer(1, "Inspiration", 1).error shouldNotBe null
            }

            test("sacrificing a creature lets the spell resolve") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Blood Funnel")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Inspiration")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val handBefore = game.handSize(1)
                game.castSpellTargetingPlayer(1, "Inspiration", 1).error shouldBe null
                game.resolveStack()

                val decision = game.state.pendingDecision
                decision.shouldNotBeNull()
                decision.shouldBeInstanceOf<SelectCardsDecision>()
                game.submitDecision(CardsSelectedResponse(decision.id, decision.options.take(1)))
                game.resolveStack()

                withClue("the Bears paid the toll and Inspiration drew its two cards") {
                    game.findPermanent("Grizzly Bears") shouldBe null
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                    // -1 for the Inspiration that left the hand, +2 for the draws
                    game.handSize(1) shouldBe handBefore + 1
                    game.isInGraveyard(1, "Inspiration") shouldBe true
                }
            }

            test("declining to sacrifice counters your own spell") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Blood Funnel")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Inspiration")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingPlayer(1, "Inspiration", 1).error shouldBe null
                game.resolveStack()
                game.state.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
                game.skipSelection()
                game.resolveStack()

                withClue("no sacrifice means the spell is countered — no draws, Bears intact") {
                    game.findPermanent("Grizzly Bears") shouldNotBe null
                    game.isInGraveyard(1, "Inspiration") shouldBe true
                    game.librarySize(1) shouldBe 2
                }
            }

            test("with no creature to sacrifice the spell is countered without a prompt") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Blood Funnel")
                    .withCardInHand(1, "Inspiration")
                    .withLandsOnBattlefield(1, "Island", 2)
                    .withCardInLibrary(1, "Forest")
                    .withCardInLibrary(1, "Forest")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpellTargetingPlayer(1, "Inspiration", 1).error shouldBe null
                game.resolveStack()

                withClue("an unpayable cost skips straight to the suffer branch") {
                    game.hasPendingDecision() shouldBe false
                    game.isInGraveyard(1, "Inspiration") shouldBe true
                    game.librarySize(1) shouldBe 2
                }
            }

            test("creature spells are neither discounted nor taxed") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Blood Funnel")
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withCardInHand(1, "Centaur Courser") // {2}{G}
                    .withLandsOnBattlefield(1, "Forest", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                withClue("no reduction applies, so the full {2}{G} must be available") {
                    game.castSpell(1, "Centaur Courser").error shouldBe null
                }
                game.resolveStack()

                withClue("and the Funnel's trigger never fires on a creature spell") {
                    game.hasPendingDecision() shouldBe false
                    game.findPermanent("Centaur Courser") shouldNotBe null
                    game.findPermanent("Grizzly Bears") shouldNotBe null
                }
            }
        }
    }
}
