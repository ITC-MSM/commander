package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Immersturm Predator (KHM #214; reprinted in Foundations #660).
 *
 * Flying
 * Whenever this creature becomes tapped, exile up to one target card from a graveyard and put a
 * +1/+1 counter on this creature.
 * Sacrifice another creature: This creature gains indestructible until end of turn. Tap it.
 *
 * Covers the "becomes tapped" trigger (fired here by attacking), the optional graveyard exile,
 * the always-applied +1/+1 counter, and the sacrifice ability whose own "Tap it" re-triggers the
 * becomes-tapped ability. All primitives already exist (BecomesTapped trigger, Move to exile,
 * AddCounters, SacrificeAnother cost, GrantKeyword, Tap).
 */
class ImmersturmPredatorScenarioTest : ScenarioTestBase() {

    init {
        context("Immersturm Predator") {

            test("attacking taps it, exiling a graveyard card and adding a +1/+1 counter") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Immersturm Predator", summoningSickness = false)
                    .withCardInGraveyard(2, "Grizzly Bears") // the graveyard card to exile
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val predator = game.findPermanent("Immersturm Predator")!!

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Immersturm Predator" to 2)).error shouldBe null

                // The becomes-tapped trigger wants "up to one target card from a graveyard".
                val graveyardCard = game.findCardsInGraveyard(2, "Grizzly Bears").first()
                if (game.getPendingDecision() is ChooseTargetsDecision) {
                    game.selectTargets(listOf(graveyardCard))
                }
                game.resolveStack()
                if (game.getPendingDecision() is ChooseTargetsDecision) {
                    game.selectTargets(listOf(graveyardCard))
                    game.resolveStack()
                }

                withClue("The targeted graveyard card is exiled") {
                    game.isInExile(2, "Grizzly Bears") shouldBe true
                    game.isInGraveyard(2, "Grizzly Bears") shouldBe false
                }
                withClue("A +1/+1 counter grew the 3/3 into a 4/4") {
                    game.state.projectedState.getPower(predator) shouldBe 4
                    game.state.projectedState.getToughness(predator) shouldBe 4
                }
            }

            test("the counter is still placed when no graveyard card is chosen") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Immersturm Predator", summoningSickness = false)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val predator = game.findPermanent("Immersturm Predator")!!

                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Immersturm Predator" to 2)).error shouldBe null

                // "Up to one" — decline the exile by choosing no target.
                if (game.getPendingDecision() is ChooseTargetsDecision) {
                    game.skipTargets()
                }
                game.resolveStack()
                if (game.getPendingDecision() is ChooseTargetsDecision) {
                    game.skipTargets()
                    game.resolveStack()
                }

                withClue("The +1/+1 counter is placed regardless of the optional exile") {
                    game.state.projectedState.getPower(predator) shouldBe 4
                    game.state.projectedState.getToughness(predator) shouldBe 4
                }
            }

            test("sacrificing a creature grants indestructible until end of turn") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Immersturm Predator", summoningSickness = false)
                    .withCardOnBattlefield(1, "Grizzly Bears") // fodder to sacrifice
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val predator = game.findPermanent("Immersturm Predator")!!
                val ability = cardRegistry.requireCard("Immersturm Predator").activatedAbilities.first()

                game.execute(
                    com.wingedsheep.engine.core.ActivateAbility(
                        playerId = game.player1Id,
                        sourceId = predator,
                        abilityId = ability.id
                    )
                ).error shouldBe null

                // The sacrifice cost may ask which creature to sacrifice; the "Tap it" effect then
                // re-triggers the becomes-tapped ability, which may offer a graveyard target.
                var guard = 0
                while (guard++ < 15) {
                    when (val decision = game.getPendingDecision()) {
                        is ChooseTargetsDecision -> game.skipTargets()
                        is SelectCardsDecision -> game.selectCards(decision.options.take(decision.minSelections))
                        is YesNoDecision -> game.answerYesNo(false)
                        null -> if (game.state.stack.isNotEmpty()) game.resolveStack() else break
                        else -> break
                    }
                }

                withClue("Immersturm Predator gains indestructible until end of turn") {
                    game.state.projectedState.hasKeyword(predator, Keyword.INDESTRUCTIBLE) shouldBe true
                }
                withClue("The fodder creature was sacrificed") {
                    game.isInGraveyard(1, "Grizzly Bears") shouldBe true
                }
            }
        }
    }
}
