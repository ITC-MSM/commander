package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Languish
 * {2}{B}{B}
 * Sorcery
 *
 * All creatures get -4/-4 until end of turn.
 */
val Languish = card("Languish") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "All creatures get -4/-4 until end of turn."

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter.AllCreatures,
            ModifyStatsEffect(-4, -4, EffectTarget.Self),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "105"
        artist = "Jeff Simpson"
        flavorText = "Life is such a fragile thing."
        imageUri = "https://cards.scryfall.io/normal/front/d/3/d3593efa-0a05-4061-9f6e-edd0a5ca9a1f.jpg?1783938340"
    }
}
