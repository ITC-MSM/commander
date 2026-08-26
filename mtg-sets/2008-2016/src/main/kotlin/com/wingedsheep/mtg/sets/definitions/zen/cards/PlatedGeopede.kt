package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Plated Geopede
 * {1}{R}
 * Creature — Insect
 * 1/1
 * First strike
 * Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn.
 *
 * Landfall is [Triggers.LandYouControlEnters] — the `ZoneChangeEvent` over
 * `GameObjectFilter.Land.youControl()` with `TriggerBinding.ANY`.
 */
val PlatedGeopede = card("Plated Geopede") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Insect"
    power = 1
    toughness = 1
    oracleText = "First strike\n" +
        "Landfall — Whenever a land you control enters, this creature gets +2/+2 until end of turn."

    keywords(Keyword.FIRST_STRIKE)

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "141"
        artist = "Eric Deschamps"
        flavorText = "\"Kor armorers buy the scales and claws. Elf oracles buy the rest.\"\n—Nablus, North Hada trapper"
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fd8b5c0c-acda-411e-9f17-6d9292628a56.jpg"
    }
}
