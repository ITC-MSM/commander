package com.wingedsheep.ai.llm.decision.handlers

import com.wingedsheep.ai.llm.AiResponseParser
import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.DecisionContext
import com.wingedsheep.engine.core.DecisionPhase
import com.wingedsheep.engine.core.TargetRequirementInfo
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.view.ClientStateTransformer
import com.wingedsheep.mtg.sets.definitions.por.PortalSet
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

/**
 * The LLM controller falls back to the engine AI when a handler's `parse` returns null. Anything it
 * returns instead is submitted as-is — and `DecisionValidators.validateTargets` rejects a response
 * that leaves a mandatory requirement out. A partial map is therefore not a best-effort answer but a
 * decision nobody ever answers: rejected by the server, no fallback, no retry.
 */
class ChooseTargetsHandlerCompletenessTest : FunSpec({

    val handler = ChooseTargetsHandler()
    val parser = AiResponseParser()

    val allCards = PortalSet.cards + PortalSet.basicLands
    val driver = GameTestDriver().apply {
        registerCards(allCards)
        initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
    }
    val state = ClientStateTransformer(driver.cardRegistry).transform(driver.state, driver.player1)

    val first = EntityId.of("first")
    val second = EntityId.of("second")

    fun decision(legalTargets: Map<Int, List<EntityId>>) = ChooseTargetsDecision(
        id = "two-requirements",
        playerId = driver.player1,
        prompt = "Choose targets",
        context = DecisionContext(phase = DecisionPhase.RESOLUTION),
        targetRequirements = listOf(
            TargetRequirementInfo(index = 0, description = "target creature"),
            TargetRequirementInfo(index = 1, description = "target creature"),
        ),
        legalTargets = legalTargets,
    )

    test("a single letter answers both requirements when both can be answered") {
        val decision = decision(mapOf(0 to listOf(first, second), 1 to listOf(first, second)))

        val response = handler.parse("A", decision, state, parser) as TargetsResponse

        response.selectedTargets shouldBe mapOf(0 to listOf(first), 1 to listOf(first))
    }

    test("a response that can only answer one of two requirements is not a parse") {
        // Requirement 1 has no legal-target list to resolve a letter against, so the fallback can
        // only fill requirement 0. Returning that half answer would wedge the decision; returning
        // null hands it to the engine AI instead.
        val decision = decision(mapOf(0 to listOf(first, second)))

        handler.parse("A", decision, state, parser).shouldBeNull()
    }
})
