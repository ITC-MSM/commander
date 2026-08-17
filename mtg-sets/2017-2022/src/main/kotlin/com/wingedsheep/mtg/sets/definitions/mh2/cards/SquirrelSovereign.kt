package com.wingedsheep.mtg.sets.definitions.mh2.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Squirrel Sovereign
 * {1}{G}
 * Creature — Squirrel Noble
 * 2/2
 *
 * Other Squirrels you control get +1/+1.
 *
 * "Squirrels" with no card type attached is every Squirrel *permanent* you control, not only
 * Squirrel creatures — hence [GameObjectFilter.Permanent] rather than `.Creature`.
 */
val SquirrelSovereign = card("Squirrel Sovereign") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Squirrel Noble"
    oracleText = "Other Squirrels you control get +1/+1."
    power = 2
    toughness = 2

    staticAbility {
        ability = ModifyStats(
            powerBonus = 1,
            toughnessBonus = 1,
            filter = GroupFilter(
                GameObjectFilter.Permanent.withSubtype(Subtype.SQUIRREL).youControl(),
                excludeSelf = true
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "175"
        artist = "Ilse Gort"
        flavorText = "For rule of the Aldering Forest, Numstail stole the acorn from Nestwind, who stole it from Brankie, who stole it from Lightroot, who stole it from Leafpaw, who stole it from Darkfur . . ."
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8f739d55-30d6-4879-872a-82c6778113de.jpg?1783926824"
    }
}
