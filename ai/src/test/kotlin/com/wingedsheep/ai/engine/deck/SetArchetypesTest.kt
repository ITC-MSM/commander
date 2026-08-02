package com.wingedsheep.ai.engine.deck

import com.wingedsheep.sdk.core.Color
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class SetArchetypesTest : FunSpec({

    test("Wilds of Eldraine exposes all ten limited color-pair archetypes") {
        val woe = SetArchetypes.getForSet("woe")

        woe?.setName shouldBe "Wilds of Eldraine"
        woe?.archetypes?.map { it.name } shouldContainExactly listOf(
            "Tap Tempo",
            "Faeries",
            "Rats",
            "Ferocious Stompy",
            "Enchanted Creatures",
            "Bargain",
            "Spells",
            "Food",
            "Celebration Aggro",
            "Big Spells",
        )
        woe?.archetypes?.map { it.colors.toSet() }?.toSet() shouldBe ALL_COLOR_PAIRS
    }
})

private val ALL_COLOR_PAIRS = setOf(
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
