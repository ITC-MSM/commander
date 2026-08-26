package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Honed Khopesh
 * {1}
 * Artifact — Equipment
 * Equipped creature gets +1/+1.
 * Equip {1} ({1}: Attach to target creature you control. Equip only as a sorcery.)
 */
val HonedKhopesh = card("Honed Khopesh") {
    manaCost = "{1}"
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +1/+1.\n" +
        "Equip {1} ({1}: Attach to target creature you control. Equip only as a sorcery.)"

    staticAbility {
        ability = ModifyStats(1, 1)
    }

    equipAbility("{1}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "230"
        artist = "Aaron Miller"
        flavorText = "Blades and bravery go hand in hand."
        imageUri = "https://cards.scryfall.io/normal/front/c/7/c7d80b98-baa2-48ea-9611-96e64d7cb950.jpg?1783936452"
    }
}
