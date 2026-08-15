package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.mtg.sets.definitions.tla.cards.InvasionSubmersible
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Invasion Submersible (TLA #57).
 *
 * {2}{U} 0/0 Artifact — Vehicle.
 * When this Vehicle enters, return up to one other target nonland permanent to its owner's hand.
 * Exhaust — Waterbend {3}: This Vehicle becomes an artifact creature. Put three +1/+1 counters on it.
 */
class InvasionSubmersibleScenarioTest : ScenarioTestBase() {

    init {
        cardRegistry.register(InvasionSubmersible)

        context("Invasion Submersible") {

            test("ETB returns up to one other target nonland permanent to its owner's hand") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardInHand(1, "Invasion Submersible")
                    .withCardOnBattlefield(2, "Hill Giant", summoningSickness = false)
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val giant = game.findPermanent("Hill Giant")!!
                game.castSpell(1, "Invasion Submersible").error shouldBe null
                game.resolveStack()
                // The ETB trigger targets the opponent's Hill Giant.
                if (game.hasPendingDecision()) game.selectTargets(listOf(giant))
                game.resolveStack()

                withClue("Hill Giant was bounced to its owner's hand") {
                    game.isOnBattlefield("Hill Giant") shouldBe false
                    game.isInHand(2, "Hill Giant") shouldBe true
                }
            }

            test("exhaust (waterbend) turns it into a 3/3 artifact creature") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Invasion Submersible")
                    .withLandsOnBattlefield(1, "Island", 3)
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val sub = game.findPermanent("Invasion Submersible")!!
                val abilityId = cardRegistry.getCard("Invasion Submersible")!!.script.activatedAbilities[0].id

                withClue("starts as a non-creature artifact Vehicle") {
                    game.state.projectedState.isCreature(sub) shouldBe false
                }

                game.execute(ActivateAbility(game.player1Id, sub, abilityId)).error shouldBe null
                if (game.getPendingDecision() is com.wingedsheep.engine.core.SelectManaSourcesDecision) {
                    game.submitManaSourcesAutoPay()
                }
                game.resolveStack()

                val projected = game.state.projectedState
                withClue("becomes an artifact creature with three +1/+1 counters (3/3)") {
                    projected.isCreature(sub) shouldBe true
                    projected.hasType(sub, "ARTIFACT") shouldBe true
                    game.state.getEntity(sub)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 3
                    projected.getPower(sub) shouldBe 3
                    projected.getToughness(sub) shouldBe 3
                }
            }
        }
    }
}
