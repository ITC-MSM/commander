package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Fists of Ironwood
 * {1}{G}
 * Enchantment — Aura
 * Enchant creature
 * When this Aura enters, create two 1/1 green Saproling creature tokens.
 * Enchanted creature has trample.
 *
 * [GrantKeyword]'s filter defaults to the attached creature, which is exactly what an Aura's
 * "Enchanted creature has ..." line means — no explicit [com.wingedsheep.sdk.dsl.Filters] needed.
 */
val FistsOfIronwood = card("Fists of Ironwood") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "When this Aura enters, create two 1/1 green Saproling creature tokens.\n" +
        "Enchanted creature has trample."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            count = 2,
            power = 1,
            toughness = 1,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Saproling")
        )
    }

    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "164"
        artist = "Glen Angus"
        flavorText = "Saprolings add the three and the four to the \"one-two punch.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/1/7193c00f-0398-485c-974d-346ee59cd4c7.jpg"
    }
}
