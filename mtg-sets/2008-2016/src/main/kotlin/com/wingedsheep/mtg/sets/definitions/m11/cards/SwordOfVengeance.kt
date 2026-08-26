package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Sword of Vengeance
 * {3}
 * Artifact — Equipment
 *
 * Equipped creature gets +2/+0 and has first strike, vigilance, trample, and haste.
 * Equip {3}
 */
val SwordOfVengeance = card("Sword of Vengeance") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "Equipped creature gets +2/+0 and has first strike, vigilance, trample, and haste.\n" +
        "Equip {3}"

    staticAbility {
        ability = ModifyStats(+2, 0, Filters.EquippedCreature)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FIRST_STRIKE, Filters.EquippedCreature)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.VIGILANCE, Filters.EquippedCreature)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE, Filters.EquippedCreature)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.HASTE, Filters.EquippedCreature)
    }

    equipAbility("{3}")

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "216"
        artist = "Dan Murayama Scott"
        flavorText = "When wielded by a true believer, it matters little whether the sword is a relic " +
            "or a replica."
        imageUri = "https://cards.scryfall.io/normal/front/9/6/96fc0138-46fc-493c-8a28-8630c4759193.jpg?1783941788"
    }
}
