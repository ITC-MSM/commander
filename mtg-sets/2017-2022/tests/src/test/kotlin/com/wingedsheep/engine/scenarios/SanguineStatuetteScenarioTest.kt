package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.scripting.AdditionalCostPayment
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Scenario test for Sanguine Statuette (VOW #177) — {1}{R} Artifact.
 *
 *   When this artifact enters, create a Blood token.
 *   Whenever you sacrifice a Blood token, you may have this artifact become a 3/3 Vampire artifact
 *   creature with haste until end of turn.
 *
 * The Blood token is sacrificed by activating its own "{1}, {T}, Discard a card, Sacrifice this
 * artifact: Draw a card" ability (the Gluttonous Guest idiom), which is what makes the
 * per-permanent sacrifice trigger fire. Covers the animation's characteristics (3/3, haste,
 * Vampire, still an artifact) and the declined "may".
 */
class SanguineStatuetteScenarioTest : ScenarioTestBase() {

    init {
        context("Sanguine Statuette") {

            test("entering the battlefield creates a Blood token") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardInHand(1, "Sanguine Statuette")
                    .withLandsOnBattlefield(1, "Mountain", 2)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                game.castSpell(1, "Sanguine Statuette").error shouldBe null
                game.resolveStack()

                withClue("a Blood token is created on entering the battlefield") {
                    game.findPermanents("Blood").size shouldBe 1
                }
            }

            test("sacrificing a Blood token may animate the statuette into a 3/3 hasty Vampire") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Sanguine Statuette")
                    .withCardOnBattlefield(1, "Blood", isToken = true)
                    .withCardInHand(1, "Grizzly Bears") // fodder to discard for Blood's cost
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val statuette = game.findPermanent("Sanguine Statuette")!!

                withClue("the statuette is not a creature before the trigger resolves") {
                    game.state.projectedState.isCreature(statuette) shouldBe false
                }

                val blood = game.findPermanent("Blood")!!
                val toDiscard = game.findCardsInHand(1, "Grizzly Bears").first()
                val bloodAbilityId = cardRegistry.getCard("Blood")!!.activatedAbilities.first().id
                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = blood,
                        abilityId = bloodAbilityId,
                        costPayment = AdditionalCostPayment(discardedCards = listOf(toDiscard))
                    )
                ).error shouldBe null
                game.resolveStack()

                withClue("the sacrifice trigger asks whether to animate") {
                    game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                }
                game.answerYesNo(true)
                game.resolveStack()

                withClue("the statuette is now a 3/3 creature with haste") {
                    game.state.projectedState.isCreature(statuette) shouldBe true
                    game.state.projectedState.getPower(statuette) shouldBe 3
                    game.state.projectedState.getToughness(statuette) shouldBe 3
                    game.state.projectedState.hasKeyword(statuette, Keyword.HASTE) shouldBe true
                }
                withClue("it keeps its artifact type and gains the Vampire creature type") {
                    game.state.projectedState.getSubtypes(statuette).contains("Vampire") shouldBe true
                }
            }

            test("declining the may leaves the statuette a noncreature artifact") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Sanguine Statuette")
                    .withCardOnBattlefield(1, "Blood", isToken = true)
                    .withCardInHand(1, "Grizzly Bears")
                    .withLandsOnBattlefield(1, "Mountain", 1)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val statuette = game.findPermanent("Sanguine Statuette")!!
                val blood = game.findPermanent("Blood")!!
                val toDiscard = game.findCardsInHand(1, "Grizzly Bears").first()
                val bloodAbilityId = cardRegistry.getCard("Blood")!!.activatedAbilities.first().id
                game.execute(
                    ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = blood,
                        abilityId = bloodAbilityId,
                        costPayment = AdditionalCostPayment(discardedCards = listOf(toDiscard))
                    )
                ).error shouldBe null
                game.resolveStack()

                game.getPendingDecision().shouldBeInstanceOf<YesNoDecision>()
                game.answerYesNo(false)
                game.resolveStack()

                withClue("declining leaves it a noncreature artifact") {
                    game.state.projectedState.isCreature(statuette) shouldBe false
                }
            }
        }
    }
}
