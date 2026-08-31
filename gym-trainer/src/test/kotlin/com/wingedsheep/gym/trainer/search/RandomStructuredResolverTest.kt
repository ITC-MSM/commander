package com.wingedsheep.gym.trainer.search

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.DecisionContext
import com.wingedsheep.engine.core.TargetRequirementInfo
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.handlers.actions.decision.DecisionValidators
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.assertions.asClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.random.Random

/**
 * The resolver is a fallback *policy*, but the search now submits its answer through the engine's
 * authoritative validator. Minimum cardinality alone does not satisfy a decision's state-dependent
 * restrictions, so the resolver has to check its own draws before handing one back.
 */
class RandomStructuredResolverTest : FunSpec({

    test("a draw that violates a state-dependent restriction is redrawn, not submitted") {
        val driver = GameTestDriver().apply {
            registerCards(TestCards.all)
            initMirrorMatch(deck = Deck.of("Forest" to 40), skipMulligans = true, startingPlayer = 0)
            passPriorityUntil(Step.PRECOMBAT_MAIN)
        }
        val player = driver.activePlayer!!
        val firstBear = driver.putCreatureOnBattlefield(player, "Grizzly Bears")
        val secondBear = driver.putCreatureOnBattlefield(player, "Grizzly Bears")
        val goblin = driver.putCreatureOnBattlefield(player, "Raging Goblin")

        // "Two target creatures with different names" — the two Bears are a legal *draw* and an
        // illegal *response*, so a resolver that only counts targets picks it one time in three.
        val decision = ChooseTargetsDecision(
            id = "different-names",
            playerId = player,
            prompt = "Choose two creatures with different names",
            context = DecisionContext(),
            targetRequirements = listOf(
                TargetRequirementInfo(
                    index = 0,
                    description = "two creatures with different names",
                    minTargets = 2,
                    maxTargets = 2,
                    differentNames = true
                )
            ),
            legalTargets = mapOf(0 to listOf(firstBear, secondBear, goblin))
        )

        // The restriction is real: the same-name pair is rejected, so the test is not vacuous.
        DecisionValidators.validate(
            decision,
            TargetsResponse(decision.id, mapOf(0 to listOf(firstBear, secondBear))),
            driver.state
        ) shouldNotBe null

        repeat(50) { seed ->
            val response = RandomStructuredResolver(Random(seed.toLong()))
                .resolve(driver.state, decision)
            response.asClue {
                DecisionValidators.validate(decision, response, driver.state) shouldBe null
            }
        }
    }
})
