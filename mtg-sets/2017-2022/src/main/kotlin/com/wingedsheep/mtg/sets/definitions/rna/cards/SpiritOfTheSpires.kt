package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Spirit of the Spires — Ravnica Allegiance #23
 * {3}{W} · Creature — Spirit · 2 / 4
 *
 * A flying lord. `excludeSelf = true` carries the printed "Other" — without it the Spirit
 * would pump itself, since it has flying too.
 */
val SpiritOfTheSpires = card("Spirit of the Spires") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit"
    power = 2
    toughness = 4
    oracleText = "Flying\n" +
        "Other creatures you control with flying get +0/+1."

    keywords(Keyword.FLYING)
    staticAbility {
        ability = ModifyStats(
            powerBonus = 0,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.withKeyword(Keyword.FLYING).youControl(), excludeSelf = true)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "23"
        artist = "Yeong-Hao Han"
        flavorText = "She breathes fair winds to tired griffins and lifts songbirds beyond the reach of stalking cats."
        imageUri = "https://cards.scryfall.io/normal/front/d/b/dbd11910-58e2-4233-a18c-e97413126597.jpg"
    }
}
