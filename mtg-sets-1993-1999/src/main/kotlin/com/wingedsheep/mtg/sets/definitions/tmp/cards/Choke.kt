package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Choke
 * {2}{G}
 * Enchantment
 * Islands don't untap during their controllers' untap steps.
 */
val Choke = card("Choke") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "Islands don't untap during their controllers' untap steps."

    staticAbility {
        ability = GrantKeyword(AbilityFlag.DOESNT_UNTAP.name, GroupFilter.allLandsWithSubtype(Subtype.ISLAND))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "219"
        artist = "Terese Nielsen"
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e2f85205-3c4f-4411-b09c-d1271be56dde.jpg?1562057333"
        flavorText = "\"One day we shall walk where once was water.\"\n—Eladamri, Lord of Leaves"
    }
}
