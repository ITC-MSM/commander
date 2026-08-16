package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Vanquisher's Axe
 * {1}
 * Artifact — Equipment
 * Equipped creature gets +2/+0.
 * Equip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)
 */
val VanquishersAxe = card("Vanquisher's Axe") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +2/+0.\nEquip {2} ({2}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = ModifyStats(+2, +0, Filters.EquippedCreature)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "240"
        artist = "Joe Slucher"
        flavorText = "When a Keldon takes a trophy from an enemy, it's usually something they can use to kill more enemies."
        imageUri = "https://cards.scryfall.io/normal/front/a/e/aef37244-deb9-464e-ab8c-4308369ae09b.jpg?1783921266"
    }
}
