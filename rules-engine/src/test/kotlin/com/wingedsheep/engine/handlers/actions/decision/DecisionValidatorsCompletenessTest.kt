package com.wingedsheep.engine.handlers.actions.decision

import com.wingedsheep.engine.core.ChooseTargetsDecision
import com.wingedsheep.engine.core.DecisionContext
import com.wingedsheep.engine.core.DecisionPhase
import com.wingedsheep.engine.core.OrderObjectsDecision
import com.wingedsheep.engine.core.OrderedResponse
import com.wingedsheep.engine.core.PilesSplitResponse
import com.wingedsheep.engine.core.ReorderLibraryDecision
import com.wingedsheep.engine.core.SplitPilesDecision
import com.wingedsheep.engine.core.TargetRequirementInfo
import com.wingedsheep.engine.core.TargetsResponse
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

/**
 * A response can be *structurally* incomplete without naming a single illegal object: a target group
 * left out entirely, or a collection that repeats one object in place of another. Set comparisons
 * can't see either — a set discards both absence-by-omission and multiplicity — so each of these
 * validators has to count as well as compare.
 */
class DecisionValidatorsCompletenessTest : FunSpec({

    val player = EntityId.of("player")
    val first = EntityId.of("first")
    val second = EntityId.of("second")
    val context = DecisionContext(phase = DecisionPhase.RESOLUTION)

    fun targetDecision() = ChooseTargetsDecision(
        id = "targets",
        playerId = player,
        prompt = "Choose targets",
        context = context,
        targetRequirements = listOf(
            TargetRequirementInfo(index = 0, description = "mandatory"),
            TargetRequirementInfo(index = 1, description = "optional", minTargets = 0),
        ),
        legalTargets = mapOf(0 to listOf(first), 1 to listOf(second)),
    )

    test("target response may omit an optional requirement but not a mandatory one") {
        val decision = targetDecision()

        DecisionValidators.validate(
            decision,
            TargetsResponse(decision.id, mapOf(0 to listOf(first))),
        ).shouldBeNull()

        DecisionValidators.validate(
            decision,
            TargetsResponse(decision.id, emptyMap()),
        ).shouldNotBeNull()
    }

    test("a mandatory requirement is missing even when another group answers") {
        // The sharper case than an empty response: the map is non-empty and every id in it is legal,
        // so only checking what was submitted sees a well-formed answer. The mandatory group 0 is
        // still unanswered.
        val decision = targetDecision()

        DecisionValidators.validate(
            decision,
            TargetsResponse(decision.id, mapOf(1 to listOf(second))),
        ).shouldNotBeNull()
    }

    test("ordering response contains every object exactly once") {
        val decision = OrderObjectsDecision(
            id = "order",
            playerId = player,
            prompt = "Order",
            context = context,
            objects = listOf(first),
        )

        DecisionValidators.validate(
            decision,
            OrderedResponse(decision.id, listOf(first)),
        ).shouldBeNull()

        DecisionValidators.validate(
            decision,
            OrderedResponse(decision.id, listOf(first, first)),
        ).shouldNotBeNull()
    }

    test("library reorder rejects a repeated card even when every card appears") {
        val decision = ReorderLibraryDecision(
            id = "reorder",
            playerId = player,
            prompt = "Reorder",
            context = context,
            cards = listOf(first, second),
            cardInfo = emptyMap(),
        )

        DecisionValidators.validate(
            decision,
            OrderedResponse(decision.id, listOf(second, first)),
        ).shouldBeNull()

        // Every card of the decision is present, so the sets match; only the count says the
        // response holds one of them twice.
        DecisionValidators.validate(
            decision,
            OrderedResponse(decision.id, listOf(first, second, first)),
        ).shouldNotBeNull()
    }

    test("pile split rejects a card put in two piles") {
        // A card can't be in two piles at once, but the pile count and the flattened *set* are both
        // right when it is — only counting the cards catches it.
        val decision = SplitPilesDecision(
            id = "piles",
            playerId = player,
            prompt = "Split",
            context = context,
            cards = listOf(first, second),
        )

        DecisionValidators.validate(
            decision,
            PilesSplitResponse(decision.id, listOf(listOf(first), listOf(second))),
        ).shouldBeNull()

        DecisionValidators.validate(
            decision,
            PilesSplitResponse(decision.id, listOf(listOf(first, second), listOf(first))),
        ).shouldNotBeNull()
    }
})
