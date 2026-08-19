package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Dragonloft Idol
 * {4}
 * Artifact Creature — Gargoyle
 * 3/3
 *
 * As long as you control a Dragon, this creature gets +1/+1 and has flying and trample.
 */
val DragonloftIdol = card("Dragonloft Idol") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Gargoyle"
    oracleText = "As long as you control a Dragon, this creature gets +1/+1 and has flying and trample."
    power = 3
    toughness = 3

    val youControlADragon = Conditions.ControlPermanentOfType(Subtype.DRAGON)

    staticAbility {
        ability = ModifyStats(1, 1, Filters.Self)
        condition = youControlADragon
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING, GroupFilter.source())
        condition = youControlADragon
    }

    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE, GroupFilter.source())
        condition = youControlADragon
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "237"
        artist = "Jung Park"
        flavorText = "The idols were forged during the time of the Khanfall, when the dragons came to rule Tarkir and its people aligned themselves with the five dragonlords."
        imageUri = "https://cards.scryfall.io/normal/front/c/7/c7463c18-5bef-42a0-a37e-6112809ebc78.jpg?1783938569"
    }
}
