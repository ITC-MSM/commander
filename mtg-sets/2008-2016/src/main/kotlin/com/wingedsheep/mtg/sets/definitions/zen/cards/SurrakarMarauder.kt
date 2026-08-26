package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Surrakar Marauder
 * {1}{B}
 * Creature — Surrakar
 * 2/1
 * Landfall — Whenever a land you control enters, this creature gains intimidate until end of turn. (It can't be blocked except by artifact creatures and/or creatures that share a color with it.)
 *
 * Landfall is [Triggers.LandYouControlEnters] — the `ZoneChangeEvent` over
 * `GameObjectFilter.Land.youControl()` with `TriggerBinding.ANY`.
 */
val SurrakarMarauder = card("Surrakar Marauder") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Surrakar"
    power = 2
    toughness = 1
    oracleText = "Landfall — Whenever a land you control enters, this creature gains intimidate until end of turn. (It can't be blocked except by artifact creatures and/or creatures that share a color with it.)"

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.GrantKeyword(Keyword.INTIMIDATE, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "113"
        artist = "Kev Walker"
        imageUri = "https://cards.scryfall.io/normal/front/2/8/28a7a1af-14f2-4e22-bd27-9207e837d5fb.jpg"
    }
}
