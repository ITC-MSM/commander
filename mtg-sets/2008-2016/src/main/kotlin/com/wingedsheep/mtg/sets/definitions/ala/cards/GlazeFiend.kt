package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern.ZoneChangeEvent
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Glaze Fiend
 * {1}{B}
 * Artifact Creature — Illusion
 * 0 / 1
 * Flying
 * Whenever another artifact you control enters, this creature gets +2/+2 until end of turn.
 *
 * The trigger is a raw [TriggerSpec] over a [ZoneChangeEvent] into [Zone.BATTLEFIELD] filtered to
 * `GameObjectFilter.Artifact.youControl()`; because Glaze Fiend is itself an artifact, the printed
 * "another" is [TriggerBinding.OTHER], which excludes the source's own entry. The pump is the plain
 * [Effects.ModifyStats] on [EffectTarget.Self], whose default `Duration.EndOfTurn` is exactly the
 * printed "until end of turn" — so a turn with several artifact drops stacks one floating +2/+2 per
 * trigger.
 */
val GlazeFiend = card("Glaze Fiend") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Artifact Creature — Illusion"
    power = 0
    toughness = 1
    oracleText = "Flying\n" +
        "Whenever another artifact you control enters, this creature gets +2/+2 until end of turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = TriggerSpec(
            event = ZoneChangeEvent(
                filter = GameObjectFilter.Artifact.youControl(),
                to = Zone.BATTLEFIELD
            ),
            binding = TriggerBinding.OTHER
        )
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "77"
        artist = "Joshua Hagler"
        flavorText = "Before the zealots of the Ethersworn came to power, Esper illusionists dreamed up creations to mimic a variety of substances."
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2e030fb1-12a8-4c28-836f-8097ec753271.jpg"
    }
}
