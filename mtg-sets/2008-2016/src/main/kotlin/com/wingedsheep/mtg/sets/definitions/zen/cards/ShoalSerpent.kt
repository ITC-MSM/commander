package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Shoal Serpent
 * {5}{U}
 * Creature — Serpent
 * 5/5
 * Defender
 * Landfall — Whenever a land you control enters, this creature loses defender until end of turn.
 *
 * Landfall is [Triggers.LandYouControlEnters] — the `ZoneChangeEvent` over
 * `GameObjectFilter.Land.youControl()` with `TriggerBinding.ANY`.
 *
 * "Loses defender" is [Effects.RemoveKeyword], a layer-6 removal that lasts until end of turn —
 * so a second land drop in the same turn is redundant, not cumulative.
 */
val ShoalSerpent = card("Shoal Serpent") {
    manaCost = "{5}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Serpent"
    power = 5
    toughness = 5
    oracleText = "Defender\n" +
        "Landfall — Whenever a land you control enters, this creature loses defender until end of turn."

    keywords(Keyword.DEFENDER)

    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        effect = Effects.RemoveKeyword(Keyword.DEFENDER, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "65"
        artist = "Trevor Claxton"
        flavorText = "\"It's like a reef that runs aground on ships.\"\n—Jaby, Silundi Sea nomad"
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a4a98047-8e6d-416c-b11d-cc4c2a56b624.jpg"
    }
}
