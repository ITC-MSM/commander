package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Windstorm Drake — Ravnica Allegiance #60
 * {4}{U} · Creature — Drake · 3 / 3
 *
 * The +1/+0 half of the same flying-lord shape as Spirit of the Spires; `excludeSelf`
 * carries the printed "Other".
 */
val WindstormDrake = card("Windstorm Drake") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Drake"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "Other creatures you control with flying get +1/+0."

    keywords(Keyword.FLYING)
    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 0,
            filter = GroupFilter(GameObjectFilter.Creature.withKeyword(Keyword.FLYING).youControl(), excludeSelf = true)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "60"
        artist = "Daarken"
        flavorText = "Drakes become especially voracious as they prepare for their autumn migration, hunting the city's thoroughfares from dawn to dusk."
        imageUri = "https://cards.scryfall.io/normal/front/1/2/120aa3b3-d358-4df3-be39-9b7ce926673a.jpg"
    }
}
