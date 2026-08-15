package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantWard
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.WardCost
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Flowering of the White Tree
 * {W}{W}
 * Legendary Enchantment
 *
 * Legendary creatures you control get +2/+1 and have ward {1}.
 * Nonlegendary creatures you control get +1/+1.
 */
val FloweringOfTheWhiteTree = card("Flowering of the White Tree") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Enchantment"
    oracleText = "Legendary creatures you control get +2/+1 and have ward {1}.\nNonlegendary creatures you control get +1/+1."

    // Legendary creatures you control get +2/+1
    staticAbility {
        ability = ModifyStats(
            powerBonus = 2,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.legendary().youControl())
        )
    }

    // ...and have ward {1}
    staticAbility {
        ability = GrantWard(
            cost = WardCost.Mana("{1}"),
            filter = GroupFilter(GameObjectFilter.Creature.legendary().youControl())
        )
    }

    // Nonlegendary creatures you control get +1/+1
    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(GameObjectFilter.Creature.nonlegendary().youControl())
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "15"
        artist = "Erikas Perl"
        flavorText = "Aragorn planted the new tree in the court by the fountain, and swiftly and gladly it began to grow."
        imageUri = "https://cards.scryfall.io/normal/front/2/2/2203b2cd-48e5-471a-85fe-dc81012e5d61.jpg?1686967772"
    }
}
