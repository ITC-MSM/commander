package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Khenra Charioteer
 * {1}{R}{G}
 * Creature — Jackal Warrior
 * 3/3
 * Trample
 * Other creatures you control have trample.
 */
val KhenraCharioteer = card("Khenra Charioteer") {
    manaCost = "{1}{R}{G}"
    colorIdentity = "GR"
    typeLine = "Creature — Jackal Warrior"
    oracleText = "Trample\n" +
            "Other creatures you control have trample."
    power = 3
    toughness = 3

    keywords(Keyword.TRAMPLE)

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.TRAMPLE,
            filter = GroupFilter(
                GameObjectFilter.Creature.youControl(),
                excludeSelf = true
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "201"
        artist = "Chris Rallis"
        flavorText = "\"We do not swerve.\"\n—Tah-crop charioteer motto"
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8ab1454e-3c69-4450-bd4c-934af2ff2bcb.jpg?1783936463"
    }
}
