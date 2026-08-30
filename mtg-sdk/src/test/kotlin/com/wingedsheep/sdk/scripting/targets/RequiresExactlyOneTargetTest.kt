package com.wingedsheep.sdk.scripting.targets

import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for [TargetRequirement.requiresExactlyOneTarget] — the predicate every engine path that
 * fills a target *without asking* now reads.
 *
 * The distinction it draws is the one that matters at a lone legal object: "target creature" has
 * nothing left to choose, while "up to one target creature" still has the empty selection
 * (CR 601.2c). Getting that wrong is silent — the player simply never sees the prompt.
 */
class RequiresExactlyOneTargetTest : FunSpec({

    test("a plain single target is settled once one legal object exists") {
        TargetObject(filter = TargetFilter.Creature).requiresExactlyOneTarget shouldBe true
        TargetPlayer().requiresExactlyOneTarget shouldBe true
    }

    test("up to one leaves the empty selection, so it is never settled") {
        TargetObject(optional = true, filter = TargetFilter.Creature)
            .requiresExactlyOneTarget shouldBe false
    }

    test("any number is never settled even though count stays at its default of one") {
        // `unlimited` leaves `count` at 1 by convention, so the minimum is what distinguishes it.
        val req = TargetObject(unlimited = true, filter = TargetFilter.Creature)
        req.count shouldBe 1
        req.requiresExactlyOneTarget shouldBe false
    }

    test("a multi-target requirement is not settled by one legal object") {
        TargetObject(count = 2, filter = TargetFilter.Creature).requiresExactlyOneTarget shouldBe false
        TargetObject(count = 2, minCount = 1, filter = TargetFilter.Creature)
            .requiresExactlyOneTarget shouldBe false
    }

    test("narrowing an optional slot to one target does not make it mandatory") {
        val narrowed = TargetObject(count = 2, optional = true, filter = TargetFilter.Creature)
            .withCount(1)
        narrowed.count shouldBe 1
        narrowed.requiresExactlyOneTarget shouldBe false
    }
})
