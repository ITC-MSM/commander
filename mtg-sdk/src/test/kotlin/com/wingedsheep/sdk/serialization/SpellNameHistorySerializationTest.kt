package com.wingedsheep.sdk.serialization

import com.wingedsheep.sdk.scripting.GameObjectFilter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SpellNameHistorySerializationTest : FunSpec({
    test("spell selection mode round-trips") {
        val mode: com.wingedsheep.sdk.scripting.effects.SelectionMode = com.wingedsheep.sdk.scripting.effects.SelectionMode.ChooseSpell
        val json = CardSerialization.json
        val serializer = com.wingedsheep.sdk.scripting.effects.SelectionMode.serializer()
        json.decodeFromString(serializer, json.encodeToString(serializer, mode)) shouldBe mode
    }
    test("cast-name history composes with a nonland filter and round-trips") {
        val filter = GameObjectFilter.Nonland.sharesNameWithSpellCastThisTurn()
        val encoded = CardSerialization.json.encodeToString(GameObjectFilter.serializer(), filter)
        CardSerialization.json.decodeFromString(GameObjectFilter.serializer(), encoded) shouldBe filter
    }
})
