package com.wingedsheep.engine.predicates

import com.wingedsheep.engine.handlers.DynamicAmountEvaluator
import com.wingedsheep.engine.handlers.EffectContext
import com.wingedsheep.engine.handlers.PredicateContext
import com.wingedsheep.engine.handlers.PredicateEvaluator
import com.wingedsheep.engine.handlers.effects.permanent.stats.contextScopedReferenceIn
import com.wingedsheep.engine.state.ComponentContainer
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.state.components.identity.ControllerComponent
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.state.components.identity.OwnerComponent
import com.wingedsheep.sdk.core.CardType
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.predicates.CardPredicate
import com.wingedsheep.sdk.scripting.values.EntityReference
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Engine wiring for the two per-object coloured-pip primitives added for Namor the Sub-Mariner:
 *
 *  - [CardPredicate.ColoredManaSymbolsAtLeast] — "a spell with one or more blue mana symbols in
 *    its mana cost", evaluated by [PredicateEvaluator].
 *  - [com.wingedsheep.sdk.scripting.values.EntityNumericProperty.ColoredManaSymbolCount] — "that
 *    many", evaluated by [DynamicAmountEvaluator].
 *
 * Both read the *printed* cost through the one shared
 * [ManaCost.coloredSymbolCount] rule (hybrid and Phyrexian pips count for their colour(s),
 * CR 107.4e/f — pinned down symbol by symbol in the SDK's `ManaCostColoredSymbolCountTest`). What
 * this file pins down is the engine's side of it: that the two agree on the same object, that a
 * face-down object has no mana cost (CR 708.2), that the predicate is a *cost* read rather than a
 * colour read, and that the amount is classified correctly by the layer projector's
 * context-scope guard.
 */
class ColoredManaSymbolCountTest : FunSpec({

    val predicateEvaluator = PredicateEvaluator()
    val amountEvaluator = DynamicAmountEvaluator()
    val player = EntityId.generate()

    /** A battlefield permanent whose only interesting characteristic is its printed cost. */
    fun stateWith(costs: List<String>, faceDownIndices: Set<Int> = emptySet()): Pair<GameState, List<EntityId>> {
        var state = GameState().withEntity(player, ComponentContainer())
        val ids = costs.map { EntityId.generate() }
        costs.forEachIndexed { index, cost ->
            var container = ComponentContainer()
                .with(
                    CardComponent(
                        cardDefinitionId = "Card$index",
                        name = "Card$index",
                        manaCost = ManaCost.parse(cost),
                        typeLine = TypeLine(cardTypes = setOf(CardType.INSTANT)),
                        ownerId = player
                    )
                )
                .with(OwnerComponent(player))
                .with(ControllerComponent(player))
            if (index in faceDownIndices) container = container.with(FaceDownComponent)
            state = state.withEntity(ids[index], container)
                .addToZone(ZoneKey(player, Zone.BATTLEFIELD), ids[index])
        }
        return state to ids
    }

    fun pipFilter(vararg colors: Color, min: Int = 1) = GameObjectFilter(
        cardPredicates = listOf(CardPredicate.ColoredManaSymbolsAtLeast(colors.toList(), min))
    )

    fun blueFilter(min: Int = 1) = pipFilter(Color.BLUE, min = min)

    fun GameState.matches(id: EntityId, filter: GameObjectFilter) =
        predicateEvaluator.matches(this, projectedState, id, filter, PredicateContext(controllerId = player))

    // Read via EntityReference.Source so the object under test is named by sourceId alone —
    // no ChosenTarget plumbing between the primitive and the assertion.
    fun GameState.pips(id: EntityId?, vararg colors: Color) = amountEvaluator.evaluate(
        this,
        DynamicAmounts.coloredManaSymbolsOf(EntityReference.Source, *colors),
        EffectContext(sourceId = id, controllerId = player)
    )

    fun GameState.bluePips(id: EntityId?) = pips(id, Color.BLUE)

    test("the predicate matches exactly the costs that carry a blue pip") {
        val costs = listOf("{1}{U}{U}", "{U}", "{2}{R}{G}", "{5}", "{X}", "", "{C}{C}")
        val (state, ids) = stateWith(costs)
        val expected = listOf(true, true, false, false, false, false, false)
        costs.indices.forEach { i ->
            withClue("'${costs[i]}' has a blue mana symbol?") {
                state.matches(ids[i], blueFilter()) shouldBe expected[i]
            }
        }
    }

    test("hybrid and Phyrexian pips are blue mana symbols (CR 107.4e/f)") {
        val costs = listOf("{U/R}", "{2/U}", "{U/P}", "{R/G}", "{2/B}", "{G/P}")
        val (state, ids) = stateWith(costs)
        val expected = listOf(true, true, true, false, false, false)
        costs.indices.forEach { i ->
            withClue("'${costs[i]}' has a blue mana symbol?") {
                state.matches(ids[i], blueFilter()) shouldBe expected[i]
            }
        }
    }

    test("min raises the threshold — 'two or more blue mana symbols'") {
        val costs = listOf("{U}", "{1}{U}{U}", "{U}{U}{U}")
        val (state, ids) = stateWith(costs)
        listOf(false, true, true).forEachIndexed { i, want ->
            withClue("'${costs[i]}' has two or more blue pips?") {
                state.matches(ids[i], blueFilter(min = 2)) shouldBe want
            }
        }
    }

    test("a two-colour request counts a pip of either, and a pip that is both only once") {
        // The multi-colour rule is pinned symbol-by-symbol in the SDK; what this checks is that
        // both engine wrappers really pass the whole colour list through (predicate.colors.toSet()
        // / property.colors.toSet()) rather than only ever seeing one colour.
        val costs = listOf("{U/R}", "{U}{R}", "{1}{R}", "{2}{G}")
        val (state, ids) = stateWith(costs)
        val expected = listOf(1, 2, 1, 0)
        costs.indices.forEach { i ->
            withClue("blue-or-red pips in '${costs[i]}'") {
                state.pips(ids[i], Color.BLUE, Color.RED) shouldBe expected[i]
            }
            withClue("'${costs[i]}' has a blue or red mana symbol?") {
                state.matches(ids[i], pipFilter(Color.BLUE, Color.RED)) shouldBe (expected[i] >= 1)
            }
        }
    }

    test("all five colours at min = 3 — Omnath, Locus of All's 'three or more colored mana symbols'") {
        val allColors = Color.entries.toTypedArray()
        // Omnath's own cost {W}{U}{B/P}{R}{G} is 5; a two-pip gold spell is not enough; a hybrid
        // that is two of the requested colours is still one symbol, so {U/R}{U/R}{U/R} is exactly 3.
        val costs = listOf("{W}{U}{B/P}{R}{G}", "{1}{W}{U}", "{U/R}{U/R}{U/R}", "{7}")
        val (state, ids) = stateWith(costs)
        val expected = listOf(5, 2, 3, 0)
        costs.indices.forEach { i ->
            withClue("colored pips in '${costs[i]}'") {
                state.pips(ids[i], *allColors) shouldBe expected[i]
            }
            withClue("'${costs[i]}' has three or more colored mana symbols?") {
                state.matches(ids[i], pipFilter(*allColors, min = 3)) shouldBe (expected[i] >= 3)
            }
        }
    }

    test("the degenerate predicate values are rejected at construction, not silently universal") {
        // min <= 0 or no colours would make coloredSymbolCount's 0 satisfy the threshold, so the
        // predicate would match every object including costless ones — never a caller's intent.
        shouldThrow<IllegalArgumentException> {
            CardPredicate.ColoredManaSymbolsAtLeast(emptyList())
        }
        shouldThrow<IllegalArgumentException> {
            CardPredicate.ColoredManaSymbolsAtLeast(listOf(Color.BLUE), min = 0)
        }
    }

    test("a face-down object has no mana cost, so it neither matches nor counts (CR 708.2a)") {
        val (state, ids) = stateWith(listOf("{1}{U}{U}"), faceDownIndices = setOf(0))
        state.matches(ids[0], blueFilter()) shouldBe false
        state.bluePips(ids[0]) shouldBe 0
    }

    test("the amount counts the pips the predicate gated on, on the same object") {
        val costs = listOf("{1}{U}{U}", "{U}", "{U/R}{U}", "{2/U}", "{U/P}", "{X}{X}{U}", "{5}", "")
        val (state, ids) = stateWith(costs)
        val expected = listOf(2, 1, 2, 1, 1, 1, 0, 0)
        costs.indices.forEach { i ->
            withClue("blue pips in '${costs[i]}'") {
                state.bluePips(ids[i]) shouldBe expected[i]
            }
            withClue("predicate and amount agree on '${costs[i]}'") {
                state.matches(ids[i], blueFilter()) shouldBe (expected[i] >= 1)
            }
        }
    }

    test("the amount is 0 for an entity that does not exist") {
        val (state, _) = stateWith(emptyList())
        state.bluePips(EntityId.generate()) shouldBe 0
        state.bluePips(null) shouldBe 0
    }

    test("the predicate reads the printed cost, not the object's colour") {
        // An object that is blue but has no blue pip (a colour indicator, devoid's inverse, a
        // layer-5 recolour) must NOT match — this is the case a naive "is it blue" implementation
        // gets wrong. Built by stamping the card's colour set apart from its printed cost.
        val blueByColourOnly = EntityId.generate()
        val state = GameState()
            .withEntity(player, ComponentContainer())
            .withEntity(
                blueByColourOnly,
                ComponentContainer()
                    .with(
                        CardComponent(
                            cardDefinitionId = "Blue Without Pips",
                            name = "Blue Without Pips",
                            manaCost = ManaCost.parse("{2}{R}"),
                            typeLine = TypeLine(cardTypes = setOf(CardType.INSTANT)),
                            colors = setOf(Color.BLUE),
                            ownerId = player
                        )
                    )
                    .with(OwnerComponent(player))
                    .with(ControllerComponent(player))
            )
            .addToZone(ZoneKey(player, Zone.BATTLEFIELD), blueByColourOnly)

        withClue("blue with no blue mana symbol in its cost does not match") {
            state.matches(blueByColourOnly, blueFilter()) shouldBe false
            state.bluePips(blueByColourOnly) shouldBe 0
        }
    }

    test("the amount is classified as context-scoped exactly when its entity reference is") {
        // The layer projector rebuilds a bare EffectContext, so a triggering-scoped amount cannot
        // be re-evaluated there; SetBaseStatsEffect(reevaluateContinuously = true) rejects it
        // rather than reading 0 forever. Namor's token count is Triggering-scoped and lives in a
        // resolution-time effect, which is fine; his *power* is a projector-safe count.
        contextScopedReferenceIn(
            DynamicAmounts.coloredManaSymbolsOf(EntityReference.Triggering, Color.BLUE)
        ) shouldBe "Triggering"
        contextScopedReferenceIn(
            DynamicAmounts.coloredManaSymbolsOf(EntityReference.Source, Color.BLUE)
        ) shouldBe null
    }
})
