package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Hanna's Custody
 * {2}{W}
 * Enchantment
 * All artifacts have shroud. (They can't be the targets of spells or abilities.)
 */
val HannasCustody = card("Hanna's Custody") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "All artifacts have shroud. (They can't be the targets of spells or abilities.)"

    staticAbility {
        ability = GrantKeyword(Keyword.SHROUD, GroupFilter(GameObjectFilter.Artifact))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "22"
        artist = "DiTerlizzi"
        flavorText = "\"I protect the Legacy with my life if necessary, for its purpose is far more important than my own.\"\n" +
            "—Hanna, *Weatherlight* navigator"
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7ea44536-ef4e-4dcf-9c1a-c1122dd00cbb.jpg"
    }
}
