package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Compulsive Research — Ravnica: City of Guilds #40, {2}{U} Sorcery
 *
 * "Target player draws three cards. Then that player discards two cards unless they discard a
 *  land card."
 *
 * The targeted twin of Wrench Mind and Thirst for Discovery, and what is under test is that *both*
 * halves run on the target rather than the caster: the draw and the discard-gate take the same
 * target handle, so the failure mode worth pinning is a card that draws for the caster and
 * discards from the victim (or the reverse). Both branches of the "unless" are asserted too — one
 * land, or two cards of any kind — since a gate that silently picks a side is invisible in the card
 * snapshot.
 *
 * Note the printed filter is any *land* card, basic or not (Thirst for Discovery demands a basic).
 */
class CompulsiveResearchScenarioTest : ScenarioTestBase() {

    init {
        context("Compulsive Research") {

            test("the target draws three, then may pitch a single land card") {
                var builder = scenario()
                    .withPlayers("Caster", "Target")
                    .withCardInHand(1, "Compulsive Research")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withCardInHand(2, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                // Three known draws for the target, plus a land they can pitch.
                repeat(3) { builder = builder.withCardInLibrary(2, "Forest") }
                repeat(3) { builder = builder.withCardInLibrary(1, "Island") }
                val game = builder.build()

                val casterHandBefore = game.handSize(1)

                game.castSpellTargetingPlayer(1, "Compulsive Research", 2).error shouldBe null
                game.resolveStack()

                // The *target* drew, not the caster (the caster only lost the spell itself).
                game.handSize(2) shouldBe 4 // 1 Bears + 3 Forests
                game.handSize(1) shouldBe casterHandBefore - 1

                // And the *target* chooses what to discard.
                val decision = game.getPendingDecision().shouldNotBeNull()
                decision.playerId shouldBe game.player2Id

                val forest = game.findCardsInHand(2, "Forest").first()
                game.selectCards(listOf(forest)).error shouldBe null

                game.isInGraveyard(2, "Forest") shouldBe true
                game.handSize(2) shouldBe 3 // one land satisfied the gate
            }

            test("without a land in the selection, two cards go") {
                var builder = scenario()
                    .withPlayers("Caster", "Target")
                    .withCardInHand(1, "Compulsive Research")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withCardInHand(2, "Grizzly Bears")
                    .withCardInHand(2, "Centaur Courser")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(3) { builder = builder.withCardInLibrary(2, "Lightning Bolt") }
                repeat(3) { builder = builder.withCardInLibrary(1, "Island") }
                val game = builder.build()

                game.castSpellTargetingPlayer(1, "Compulsive Research", 2).error shouldBe null
                game.resolveStack()

                game.handSize(2) shouldBe 5 // Bears + Courser + 3 Bolts, no land drawn
                game.getPendingDecision().shouldNotBeNull()

                val bears = game.findCardsInHand(2, "Grizzly Bears").single()
                val courser = game.findCardsInHand(2, "Centaur Courser").single()
                game.selectCards(listOf(bears, courser)).error shouldBe null

                game.isInGraveyard(2, "Grizzly Bears") shouldBe true
                game.isInGraveyard(2, "Centaur Courser") shouldBe true
                game.handSize(2) shouldBe 3
            }

            test("a nonbasic land satisfies the gate — the filter is any land, not a basic") {
                var builder = scenario()
                    .withPlayers("Caster", "Target")
                    .withCardInHand(1, "Compulsive Research")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withCardInHand(2, "Great Furnace") // nonbasic artifact land
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(3) { builder = builder.withCardInLibrary(2, "Lightning Bolt") }
                repeat(3) { builder = builder.withCardInLibrary(1, "Island") }
                val game = builder.build()

                game.castSpellTargetingPlayer(1, "Compulsive Research", 2).error shouldBe null
                game.resolveStack()
                game.getPendingDecision().shouldNotBeNull()

                val furnace = game.findCardsInHand(2, "Great Furnace").single()
                game.selectCards(listOf(furnace)).error shouldBe null

                game.isInGraveyard(2, "Great Furnace") shouldBe true
                game.handSize(2) shouldBe 3 // one nonbasic land was enough
            }

            test("the caster can target themselves") {
                var builder = scenario()
                    .withPlayers("Caster", "Opponent")
                    .withCardInHand(1, "Compulsive Research")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                repeat(4) { builder = builder.withCardInLibrary(1, "Forest") }
                repeat(3) { builder = builder.withCardInLibrary(2, "Island") }
                val game = builder.build()

                game.castSpellTargetingPlayer(1, "Compulsive Research", 1).error shouldBe null
                game.resolveStack()

                game.handSize(1) shouldBe 3 // the spell left, three Forests arrived

                val decision = game.getPendingDecision().shouldNotBeNull()
                decision.playerId shouldBe game.player1Id

                val forest = game.findCardsInHand(1, "Forest").first()
                game.selectCards(listOf(forest)).error shouldBe null
                game.handSize(1) shouldBe 2
            }
        }
    }
}
