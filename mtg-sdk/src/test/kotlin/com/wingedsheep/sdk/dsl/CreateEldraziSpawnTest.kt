package com.wingedsheep.sdk.dsl

import com.wingedsheep.sdk.scripting.effects.CreatePredefinedTokenEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class CreateEldraziSpawnTest : FunSpec({

    test("dynamic count delegates to the predefined-token effect") {
        val effect = Effects.CreateEldraziSpawn(
            count = DynamicAmount.XValue,
            controller = EffectTarget.TargetController,
            imageUri = "spawn.jpg"
        ).shouldBeInstanceOf<CreatePredefinedTokenEffect>()

        effect.tokenType shouldBe "Eldrazi Spawn"
        effect.dynamicCount shouldBe DynamicAmount.XValue
        effect.controller shouldBe EffectTarget.TargetController
        effect.imageUri shouldBe "spawn.jpg"
    }
})
