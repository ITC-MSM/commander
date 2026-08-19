package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Marauder's Axe
 * {2}
 * Artifact — Equipment
 *
 * Equipped creature gets +2/+0.
 * Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)
 */
val MaraudersAxe = card("Marauder's Axe") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +2/+0.\nEquip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = ModifyStats(2, 0, Filters.EquippedCreature)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "240"
        artist = "Mitchell Malloy"
        flavorText = "A sharp axe solves most problems."
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a2ca16ee-e415-4270-a453-47111d07a07f.jpg?1783934509"
    }
}
