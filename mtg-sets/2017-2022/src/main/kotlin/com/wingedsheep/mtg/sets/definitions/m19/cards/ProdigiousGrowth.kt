package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Prodigious Growth
 * {4}{G}{G}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +7/+7 and has trample.
 */
val ProdigiousGrowth = card("Prodigious Growth") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +7/+7 and has trample."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(7, 7)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "194"
        artist = "Svetlin Velinov"
        flavorText = "\"Look how cute it is now!\"\n" +
            "—Vivien Reid"
        imageUri = "https://cards.scryfall.io/normal/front/b/a/ba2b0966-4e9e-44dc-9145-3c1e644578bc.jpg"
    }
}
