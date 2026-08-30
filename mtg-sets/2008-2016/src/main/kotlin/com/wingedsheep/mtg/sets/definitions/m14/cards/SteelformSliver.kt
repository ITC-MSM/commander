package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Steelform Sliver
 * {2}{W}
 * Creature — Sliver
 * 2 / 2
 * Sliver creatures you control get +0/+1.
 */
val SteelformSliver = card("Steelform Sliver") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Sliver"
    power = 2
    toughness = 2
    oracleText = "Sliver creatures you control get +0/+1."

    staticAbility {
        ability = ModifyStats(
            powerBonus = 0,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.withSubtype(Subtype.SLIVER).youControl())
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "38"
        artist = "Chase Stone"
        flavorText = "\"Though the slivers may sometimes resemble us, they are not human. Anyone who fights them must remember this.\"\n" +
            "—Sarlena, paladin of the Northern Verge"
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c15d6329-ffb1-43fd-8558-60c8315f5b91.jpg"
    }
}
