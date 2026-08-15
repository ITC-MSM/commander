package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.SetBasePowerToughnessDynamicStatic
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Aettir and Priwen
 * {6}
 * Legendary Artifact — Equipment
 * Equipped creature has base power and toughness X/X, where X is your life total.
 * Equip {5}
 *
 * "Your life total" = the Equipment's controller's life total. Modeled as a
 * characteristic-defining base-P/T set (Layer 7b) on the attached creature, with the
 * value recomputed continuously from the controller's current life total via
 * [DynamicAmount.YourLifeTotal] (reads the controller from projection context).
 */
val AettirAndPriwen = card("Aettir and Priwen") {
    manaCost = "{6}"
    colorIdentity = ""
    typeLine = "Legendary Artifact — Equipment"
    oracleText = "Equipped creature has base power and toughness X/X, where X is your life total.\nEquip {5}"

    staticAbility {
        ability = SetBasePowerToughnessDynamicStatic(
            power = DynamicAmount.YourLifeTotal,
            toughness = DynamicAmount.YourLifeTotal,
            filter = GroupFilter.attachedCreature(),
        )
    }

    equipAbility("{5}")

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "253"
        artist = "Vilhelmas Banys"
        flavorText = "\"The pinnacle o' perfection, forged from blood, sweat, an' tears!\"\n—Gerolt Blackthorne"
        imageUri = "https://cards.scryfall.io/normal/front/0/3/038710ca-c756-4e66-a9de-278e676c9f5b.jpg?1748706739"
    }
}
