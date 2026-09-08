package com.wingedsheep.sdk.scripting.targets

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.scripting.GameObjectFilter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FilteredAnyTargetTest : FunSpec({
    test("filtered any target round trips as a polymorphic target requirement") {
        val requirement = Targets.Any(GameObjectFilter.Any.wasDealtDamageThisTurn())
        Json.decodeFromString<TargetRequirement>(Json.encodeToString(requirement)) shouldBe requirement
    }

    test("unfiltered any targets retain their existing serialized shape") {
        Json.encodeToString<TargetRequirement>(AnyTarget()) shouldBe "{\"type\":\"AnyTarget\"}"
    }

    test("naming and count changes preserve the filter") {
        val filter = GameObjectFilter.Any.wasDealtDamageThisTurn()
        val requirement = Targets.Any(filter).withCount(2).withId("recipients") as AnyTarget
        requirement.filter shouldBe filter
        requirement.count shouldBe 2
        requirement.id shouldBe "recipients"
    }
})
