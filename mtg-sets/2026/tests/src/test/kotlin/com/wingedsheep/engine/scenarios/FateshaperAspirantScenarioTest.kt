package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseOptionDecision
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.OptionChosenResponse
import com.wingedsheep.engine.core.SelectManaSourcesDecision
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Fateshaper Aspirant (FRA #6) — {4}{W} Creature — Rhino Cleric, 3/4.
 *
 * "When this creature enters, choose one —
 *  • Return target legendary card from your graveyard to your hand.
 *  • Put a +1/+1 counter on target creature. It gains vigilance and indestructible until end of turn."
 *
 * Pins the legendary-card graveyard filter (a nonlegendary card is not a legal target) and the
 * counter + two-keyword rider on the same target.
 */
class FateshaperAspirantScenarioTest : ScenarioTestBase() {

    private fun plusOneCounters(game: TestGame, id: EntityId): Int =
        game.state.getEntity(id)?.get<CountersComponent>()?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0

    init {
        fun TestGame.castAndPickMode(modeText: String) {
            castSpell(1, "Fateshaper Aspirant").error shouldBe null
            if (getPendingDecision() is SelectManaSourcesDecision) submitManaSourcesAutoPay()
            resolveStack()
            val modeDecision = getPendingDecision() as? ChooseOptionDecision
                ?: error("expected a ChooseOptionDecision for the ETB; got ${getPendingDecision()}")
            // Modes with no legal target are left out of the offered list, so pick by label, not index.
            val index = modeDecision.options.indexOfFirst { it.contains(modeText) }
            withClue("mode '$modeText' offered in ${modeDecision.options}") { (index >= 0) shouldBe true }
            submitDecision(OptionChosenResponse(modeDecision.id, optionIndex = index))
        }

        context("Fateshaper Aspirant") {

            test("mode one returns a legendary card from your graveyard; nonlegendary cards are not legal") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardInHand(1, "Fateshaper Aspirant")
                    .withLandsOnBattlefield(1, "Plains", 5)
                    .withCardInGraveyard(1, "Kiora of Fire and Ashes") // legendary
                    .withCardInGraveyard(1, "Grizzly Bears")           // nonlegendary
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val kiora = game.findCardsInGraveyard(1, "Kiora of Fire and Ashes").single()
                val bears = game.findCardsInGraveyard(1, "Grizzly Bears").single()

                game.castAndPickMode("legendary card")

                val targetDecision = game.getPendingDecision() as? ChooseTargetsDecision
                    ?: error("expected a ChooseTargetsDecision; got ${game.getPendingDecision()}")
                val legal = targetDecision.legalTargets[0].orEmpty()
                withClue("the legendary card is a legal target") { legal.contains(kiora) shouldBe true }
                withClue("the nonlegendary card is not") { legal.contains(bears) shouldBe false }

                game.selectTargets(listOf(kiora))
                game.resolveStack()

                withClue("Kiora returned to hand") {
                    game.isInHand(1, "Kiora of Fire and Ashes") shouldBe true
                }
                withClue("Grizzly Bears stays in the graveyard") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }
            }

            test("mode two puts a +1/+1 counter on the target and grants vigilance and indestructible") {
                val game = scenario()
                    .withPlayers("You", "Opponent")
                    .withCardInHand(1, "Fateshaper Aspirant")
                    .withLandsOnBattlefield(1, "Plains", 5)
                    .withCardOnBattlefield(1, "Grizzly Bears")
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bears = game.findPermanent("Grizzly Bears")!!

                game.castAndPickMode("+1/+1 counter")

                game.getPendingDecision() as? ChooseTargetsDecision
                    ?: error("expected a ChooseTargetsDecision; got ${game.getPendingDecision()}")
                game.selectTargets(listOf(bears))
                game.resolveStack()

                withClue("one +1/+1 counter on Grizzly Bears") { plusOneCounters(game, bears) shouldBe 1 }
                val projected = game.state.projectedState
                withClue("Grizzly Bears is 3/3") {
                    projected.getPower(bears) shouldBe 3
                    projected.getToughness(bears) shouldBe 3
                }
                withClue("Grizzly Bears gains vigilance and indestructible") {
                    projected.hasKeyword(bears, Keyword.VIGILANCE) shouldBe true
                    projected.hasKeyword(bears, Keyword.INDESTRUCTIBLE) shouldBe true
                }
                withClue("the Aspirant itself is not granted anything") {
                    val aspirant = game.findPermanent("Fateshaper Aspirant")!!
                    projected.hasKeyword(aspirant, Keyword.INDESTRUCTIBLE) shouldBe false
                }
            }
        }
    }
}
