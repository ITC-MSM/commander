package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Jackhammer
 * {1}{R}
 * Artifact — Equipment
 * Equipped creature gets +2/+0.
 * Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)
 */
val Jackhammer = card("Jackhammer") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +2/+0.\nEquip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = ModifyStats(+2, +0, Filters.EquippedCreature)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "111"
        artist = "John Severin Brassell"
        flavorText = "\"Subtlety is overrated.\"\n—Mr. Orfeo, the Boulder"
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d74ba9fe-2dcb-4da7-ba64-cd932edb5b24.jpg?1783923118"
    }
}
