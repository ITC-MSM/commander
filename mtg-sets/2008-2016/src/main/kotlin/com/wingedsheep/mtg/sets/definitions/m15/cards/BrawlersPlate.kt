package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Brawler's Plate
 * {3}
 * Artifact — Equipment
 * Equipped creature gets +2/+2 and has trample.
 * Equip {4}
 */
val BrawlersPlate = card("Brawler's Plate") {
    manaCost = "{3}"
    typeLine = "Artifact — Equipment"
    oracleText =
        "Equipped creature gets +2/+2 and has trample. (It can deal excess combat damage to the player or planeswalker it's attacking.)\n" +
        "Equip {4} ({4}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = ModifyStats(+2, +2, Filters.EquippedCreature)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE, Filters.EquippedCreature)
    }

    equipAbility("{4}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "213"
        artist = "Jung Park"
        imageUri = "https://cards.scryfall.io/normal/front/e/d/eddc4ee6-7855-4dc7-9488-5e019609bd09.jpg?1783939159"
    }
}
