package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fire Urchin
 * {1}{R}
 * Creature — Elemental
 * 1/3
 * Trample
 * Whenever you cast an instant or sorcery spell, this creature gets +1/+0 until end of turn.
 */
val FireUrchin = card("Fire Urchin") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Elemental"
    oracleText = "Trample\n" +
        "Whenever you cast an instant or sorcery spell, this creature gets +1/+0 until end of turn."
    power = 1
    toughness = 3

    keywords(Keyword.TRAMPLE)
    triggeredAbility {
        trigger = Triggers.YouCastInstantOrSorcery
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "101"
        artist = "Deruchenko Alexander"
        flavorText = "Rain runoff in the Smelting District is known to spontaneously burst into flame."
        imageUri = "https://cards.scryfall.io/normal/front/b/3/b3a843ff-6bd0-42d5-9348-44b774d438b1.jpg?1783934164"
    }
}
