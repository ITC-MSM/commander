package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Trusty Machete
 * {1}
 * Artifact — Equipment
 * Equipped creature gets +2/+1.
 * Equip {2}
 */
val TrustyMachete = card("Trusty Machete") {
    manaCost = "{1}"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +2/+1.\n" +
        "Equip {2}"

    staticAbility {
        ability = ModifyStats(2, 1)
    }

    equipAbility("{2}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "209"
        artist = "Raymond Swanland"
        flavorText = "\"Until this expedition is done, that blade is your guardian, your liberator, and your best friend all rolled into one.\"\n—Yon Basrel, Oran-Rief survivalist"
        imageUri = "https://cards.scryfall.io/normal/front/8/6/86c9838e-aa72-49fc-bae2-f880bcbc9313.jpg"
    }
}
