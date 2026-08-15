package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.CountersComponent
import com.wingedsheep.engine.support.ScenarioTestBase
import com.wingedsheep.sdk.core.CounterType
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Phase
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Scenario tests for Formation Breaker (TDM #143).
 *
 * "Creatures with power less than this creature's power can't block it.
 *  As long as you control a creature with a counter on it, this creature gets +1/+2."
 *
 * Exercises the new attacker-side [com.wingedsheep.sdk.scripting.CantBeBlockedByCreaturesWithLessPower]
 * static (the dual of Spitfire Handler) and its interaction with the conditional +1/+2 buff:
 *  - A lower-power blocker can't legally block Formation Breaker.
 *  - An equal/greater-power blocker can.
 *  - When you control a creature with a counter, the buff raises Formation Breaker's power, which in
 *    turn raises the evasion threshold (a blocker that could block the 2/1 can no longer block the 3/3).
 */
class FormationBreakerScenarioTest : ScenarioTestBase() {

    init {
        // Plain vanilla blockers of various sizes (TestCards aren't in the scenario registry).
        cardRegistry.register(
            CardDefinition.creature("Small Blocker", ManaCost.parse("{1}"), emptySet(), power = 1, toughness = 1)
        )
        cardRegistry.register(
            CardDefinition.creature("Equal Blocker", ManaCost.parse("{2}"), emptySet(), power = 2, toughness = 2)
        )
        cardRegistry.register(
            CardDefinition.creature("Big Blocker", ManaCost.parse("{3}"), emptySet(), power = 3, toughness = 3)
        )

        context("Formation Breaker") {

            test("a creature with less power can't block it") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Formation Breaker")
                    .withCardOnBattlefield(2, "Small Blocker")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Formation Breaker" to 2))
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

                val block = game.declareBlockers(mapOf("Small Blocker" to listOf("Formation Breaker")))
                withClue("A power-1 blocker can't block a power-2 Formation Breaker") {
                    block.error shouldNotBe null
                }
            }

            test("a creature with equal power can block it") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Formation Breaker")
                    .withCardOnBattlefield(2, "Equal Blocker")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                game.declareAttackers(mapOf("Formation Breaker" to 2))
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

                val block = game.declareBlockers(mapOf("Equal Blocker" to listOf("Formation Breaker")))
                withClue("A power-2 blocker can legally block a power-2 Formation Breaker: ${block.error}") {
                    block.error shouldBe null
                }
            }

            test("counter buff raises the evasion threshold: a power-2 blocker can no longer block") {
                val game = scenario()
                    .withPlayers("Player", "Opponent")
                    .withCardOnBattlefield(1, "Formation Breaker")
                    .withCardOnBattlefield(1, "Small Blocker") // a creature you control to carry a counter
                    .withCardOnBattlefield(2, "Equal Blocker")
                    .withActivePlayer(1)
                    .inPhase(Phase.COMBAT, Step.DECLARE_ATTACKERS)
                    .build()

                // Put a +1/+1 counter on a creature you control to switch on Formation Breaker's +1/+2.
                val counterHolder = game.findPermanent("Small Blocker")!!
                game.state = game.state.updateEntity(counterHolder) {
                    it.with(CountersComponent(mapOf(CounterType.PLUS_ONE_PLUS_ONE to 1)))
                }

                game.declareAttackers(mapOf("Formation Breaker" to 2))
                game.advanceToPhase(Phase.COMBAT, Step.DECLARE_BLOCKERS)

                val block = game.declareBlockers(mapOf("Equal Blocker" to listOf("Formation Breaker")))
                withClue("With the +1/+2 buff, Formation Breaker is a 3/3; a power-2 blocker can't block it") {
                    block.error shouldNotBe null
                }
            }
        }
    }
}
