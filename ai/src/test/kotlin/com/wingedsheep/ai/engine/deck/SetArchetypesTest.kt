package com.wingedsheep.ai.engine.deck

import com.wingedsheep.sdk.core.Color
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class SetArchetypesTest : FunSpec({
    test("Aetherdrift exposes all ten two-color limited archetypes") {
        val synergies = requireNotNull(SetArchetypes.getForSet("dft"))

        synergies.setName shouldBe "Aetherdrift"
        synergies.archetypes.map { it.name } shouldContainExactly listOf(
            "Artifact Value",
            "Artifact Bleeder",
            "Max Speed Aggro",
            "Exhaust Midrange",
            "Vehicles and Mounts Midrange",
            "Max Speed Attrition",
            "Discard Aggro",
            "Graveyard",
            "Vehicles and Mounts Aggro",
            "Exhaust Ramp",
        )
        synergies.archetypes.map { it.colors.toSet() }.toSet() shouldBe setOf(
            setOf(Color.WHITE, Color.BLUE),
            setOf(Color.BLUE, Color.BLACK),
            setOf(Color.BLACK, Color.RED),
            setOf(Color.RED, Color.GREEN),
            setOf(Color.GREEN, Color.WHITE),
            setOf(Color.WHITE, Color.BLACK),
            setOf(Color.BLUE, Color.RED),
            setOf(Color.BLACK, Color.GREEN),
            setOf(Color.RED, Color.WHITE),
            setOf(Color.GREEN, Color.BLUE),
        )
    }
})
