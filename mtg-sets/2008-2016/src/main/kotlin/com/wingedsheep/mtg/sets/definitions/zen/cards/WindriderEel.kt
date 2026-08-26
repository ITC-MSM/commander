package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Windrider Eel
 * {3}{U}
 * Creature — Fish
 * 2/2
 * Flying
 * Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn.
 *
 * Landfall is [Triggers.LandYouControlEnters] — the `ZoneChangeEvent` over
 * `GameObjectFilter.Land.youControl()` with `TriggerBinding.ANY`.
 */
val WindriderEel = card("Windrider Eel") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Fish"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
        "Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "78"
        artist = "Austin Hsu"
        flavorText = "\"The best spot to hook one is right behind the gills.\"\n—Rana Cloudwake, kor skyfisher"
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c18b11f0-19cc-4169-84e0-fbb038a5c848.jpg"
    }
}
