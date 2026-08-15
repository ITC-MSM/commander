package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Etali's Favor
 * {2}{R}
 * Enchantment — Aura
 * Enchant creature you control
 * When this Aura enters, discover 3.
 * Enchanted creature gets +1/+1 and has trample.
 */
val EtalisFavor = card("Etali's Favor") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature you control\nWhen this Aura enters, discover 3.\nEnchanted creature gets +1/+1 and has trample."

    auraTarget = Targets.CreatureYouControl

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Discover(3)
    }

    staticAbility {
        ability = ModifyStats(1, 1)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "149"
        artist = "Matt Stewart"
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6d4075a8-c15d-4078-bf4b-0f85c03fecec.jpg?1782694490"
    }
}
