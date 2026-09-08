package com.wingedsheep.sdk.serialization

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.conditions.Condition
import com.wingedsheep.sdk.scripting.effects.Effect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.EntityReference
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class LibraryTopSerializationTest : FunSpec({
    test("library-top references compose with filters, conditions, and zone effects") {
        val reference: EntityReference = EntityReference.LibraryTop(Player.AnOpponent)
        val json = CardSerialization.json
        json.decodeFromString<EntityReference>(json.encodeToString(reference)) shouldBe reference
        val condition = Conditions.EntityMatches(EffectTarget.LibraryTop(),
            GameObjectFilter.Creature.sharingColorWith(reference))
        json.decodeFromString<Condition>(json.encodeToString(condition)) shouldBe condition
        val effect = Effects.PutOnBottomOfLibrary(EffectTarget.LibraryTop(Player.AnOpponent))
        json.decodeFromString<Effect>(json.encodeToString(effect)) shouldBe effect
    }
})
