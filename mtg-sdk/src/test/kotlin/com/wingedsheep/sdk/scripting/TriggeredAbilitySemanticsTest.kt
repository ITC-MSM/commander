package com.wingedsheep.sdk.scripting

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPlayer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TriggeredAbilitySemanticsTest : FunSpec({

    test("recognizes a triggering-player effect reference") {
        TriggeredAbility.create(
            trigger = Triggers.EachOpponentUpkeep.event,
            effect = Effects.LoseLife(1, EffectTarget.PlayerRef(Player.TriggeringPlayer)),
            activeZones = setOf(Zone.COMMAND),
        ).referencesTriggeringPlayer shouldBe true
    }

    test("recognizes an intervening-if triggering-player reference") {
        TriggeredAbility.create(
            trigger = Triggers.EachOpponentUpkeep.event,
            triggerCondition = Conditions.TriggeringPlayerIs(Player.TriggeringPlayer),
            effect = Effects.DrawCards(1),
        ).referencesTriggeringPlayer shouldBe true
    }

    test("does not infer a reference from presentation or arbitrary identifiers") {
        TriggeredAbility.create(
            trigger = Triggers.EachOpponentUpkeep.event,
            effect = Effects.DrawCards(1),
            descriptionOverride = "TriggeringPlayer",
        ).referencesTriggeringPlayer shouldBe false

        TriggeredAbility.create(
            trigger = Triggers.EachOpponentUpkeep.event,
            targetRequirement = TargetPlayer(id = "TriggeringPlayer"),
            effect = Effects.DrawCards(1),
        ).referencesTriggeringPlayer shouldBe false
    }
})
