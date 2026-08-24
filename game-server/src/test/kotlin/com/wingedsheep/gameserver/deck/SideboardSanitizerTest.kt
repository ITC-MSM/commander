package com.wingedsheep.gameserver.deck

import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.model.CardDefinition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Unit tests for [SideboardSanitizer] — the guard that keeps an unimplemented sideboard card from
 * reaching `GameInitializer`, where `CardRegistry.requireCard` would throw and fail the whole game
 * start. A pasted Arena sideboard routinely names cards the corpus hasn't reached yet.
 */
class SideboardSanitizerTest : FunSpec({

    val registry = CardRegistry().apply {
        register(CardDefinition.sorcery("Lightning Bolt", ManaCost.parse("{R}"), ""))
        register(CardDefinition.sorcery("Spell Pierce", ManaCost.parse("{U}"), ""))
    }

    test("keeps the cards the registry knows") {
        val result = SideboardSanitizer.sanitize(
            mapOf("Lightning Bolt" to 2, "Spell Pierce" to 1),
            registry,
        )
        result.kept shouldBe mapOf("Lightning Bolt" to 2, "Spell Pierce" to 1)
        result.dropped shouldBe emptyList()
        result.hasDrops shouldBe false
    }

    test("drops unimplemented cards and reports them, keeping the rest") {
        val result = SideboardSanitizer.sanitize(
            mapOf("Lightning Bolt" to 2, "Iroh's Demonstration" to 1, "Octopus Form" to 1),
            registry,
        )
        // The playable half survives — one unimplemented card must not cost the player the game.
        result.kept shouldBe mapOf("Lightning Bolt" to 2)
        result.dropped shouldBe listOf("Iroh's Demonstration", "Octopus Form")
        result.hasDrops shouldBe true
    }

    test("drops non-positive counts, which can never produce a card") {
        val result = SideboardSanitizer.sanitize(
            mapOf("Lightning Bolt" to 0, "Spell Pierce" to -1),
            registry,
        )
        result.kept shouldBe emptyMap()
        // Not "dropped" in the reportable sense — the card exists, the count was empty.
        result.dropped shouldBe emptyList()
    }

    test("an empty sideboard stays empty") {
        val result = SideboardSanitizer.sanitize(emptyMap(), registry)
        result.kept shouldBe emptyMap()
        result.hasDrops shouldBe false
    }
})
