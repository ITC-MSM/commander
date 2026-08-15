package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.DistributeDecision
import com.wingedsheep.engine.core.DistributionResponse
import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/**
 * Omnivorous Flytrap (DSK #192) — {2}{G} 2/4 Creature — Plant.
 *
 * "Delirium — Whenever this creature enters or attacks, if there are four or more card types among
 *  cards in your graveyard, distribute two +1/+1 counters among one or two target creatures. Then if
 *  there are six or more card types among cards in your graveyard, double the number of +1/+1
 *  counters on those creatures."
 *
 * Exercises:
 *  - the attack trigger gated by delirium (≥ 4 card types) distributing two +1/+1 counters;
 *  - the ≥ 6 card-types clause doubling the counters on the chosen targets;
 *  - the ≥ 4-but-< 6 case (distribute, no doubling);
 *  - the no-delirium case (trigger does nothing).
 */
class OmnivorousFlytrapScenarioTest : ScenarioTestBase() {

    private fun ScenarioBuilder.withFourTypes(): ScenarioBuilder = this
        .withCardInGraveyard(1, "Ornithopter")    // Artifact
        .withCardInGraveyard(1, "Grizzly Bears")  // Creature
        .withCardInGraveyard(1, "Lightning Bolt") // Instant
        .withCardInGraveyard(1, "Forest")         // Land

    private fun ScenarioBuilder.withSixTypes(): ScenarioBuilder = withFourTypes()
        .withCardInGraveyard(1, "Pacifism")       // Enchantment
        .withCardInGraveyard(1, "Divination")     // Sorcery

    init {
        context("Omnivorous Flytrap — delirium attack trigger") {

            test("with four card types, distributes two +1/+1 counters (no doubling)") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Omnivorous Flytrap", tapped = false, summoningSickness = false)
                    .withCardOnBattlefield(1, "Centaur Courser") // distribute target
                    .withFourTypes()
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bear = game.findPermanent("Centaur Courser")!!

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Omnivorous Flytrap" to 2))

                // Choose the single target, then distribute both counters onto it.
                var guard = 0
                while (game.getPendingDecision() == null && guard++ < 20) game.resolveStack()
                game.selectTargets(listOf(bear)).error shouldBe null
                game.resolveStack()

                if (game.hasPendingDecision()) {
                    val decision = game.getPendingDecision() as DistributeDecision
                    game.submitDecision(DistributionResponse(decision.id, mapOf(bear to decision.totalAmount)))
                    game.resolveStack()
                }

                withClue("Two +1/+1 counters on the target, not doubled (< 6 types)") {
                    game.state.getEntity(bear)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 2
                }
            }

            test("with six card types, doubles the counters on those creatures") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Omnivorous Flytrap", tapped = false, summoningSickness = false)
                    .withCardOnBattlefield(1, "Centaur Courser")
                    .withSixTypes()
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bear = game.findPermanent("Centaur Courser")!!

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Omnivorous Flytrap" to 2))

                var guard = 0
                while (game.getPendingDecision() == null && guard++ < 20) game.resolveStack()
                game.selectTargets(listOf(bear)).error shouldBe null
                game.resolveStack()

                if (game.hasPendingDecision()) {
                    val decision = game.getPendingDecision() as DistributeDecision
                    game.submitDecision(DistributionResponse(decision.id, mapOf(bear to decision.totalAmount)))
                    game.resolveStack()
                }

                withClue("Two counters distributed then doubled to four (≥ 6 types)") {
                    game.state.getEntity(bear)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) shouldBe 4
                }
            }

            test("without delirium, the attack trigger does nothing") {
                val game = scenario()
                    .withPlayers("Player1", "Player2")
                    .withCardOnBattlefield(1, "Omnivorous Flytrap", tapped = false, summoningSickness = false)
                    .withCardOnBattlefield(1, "Centaur Courser")
                    .withCardInGraveyard(1, "Grizzly Bears") // only one card type
                    .withActivePlayer(1)
                    .inPhase(Phase.PRECOMBAT_MAIN, Step.PRECOMBAT_MAIN)
                    .build()

                val bear = game.findPermanent("Centaur Courser")!!

                game.passUntilPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                game.declareAttackers(mapOf("Omnivorous Flytrap" to 2))
                game.resolveStack()

                withClue("No delirium → no target/distribute decision and no counters") {
                    (game.getPendingDecision() is DistributeDecision) shouldBe false
                    game.state.getEntity(bear)?.get<CountersComponent>()
                        ?.getCount(CounterType.PLUS_ONE_PLUS_ONE) ?: 0 shouldBe 0
                }
            }
        }
    }
}
