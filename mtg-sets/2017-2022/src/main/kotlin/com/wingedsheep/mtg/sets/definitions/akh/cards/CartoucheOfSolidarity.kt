package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Cartouche of Solidarity
 * {W}
 * Enchantment — Aura Cartouche
 * Enchant creature you control
 * When this Aura enters, create a 1/1 white Warrior creature token with vigilance.
 * Enchanted creature gets +1/+1 and has first strike. (It deals combat damage before creatures without first strike.)
 */
val CartoucheOfSolidarity = card("Cartouche of Solidarity") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura Cartouche"
    oracleText = "Enchant creature you control\n" +
        "When this Aura enters, create a 1/1 white Warrior creature token with vigilance.\n" +
        "Enchanted creature gets +1/+1 and has first strike. (It deals combat damage before creatures without first strike.)"

    auraTarget = Targets.CreatureYouControl

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Warrior"),
            keywords = setOf(Keyword.VIGILANCE),
        )
    }

    staticAbility {
        ability = ModifyStats(1, 1)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FIRST_STRIKE)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "7"
        artist = "Kieran Yanner"
        imageUri = "https://cards.scryfall.io/normal/front/9/0/90eaf94e-85a7-4958-aa58-8e2fe44db58d.jpg?1783936543"
    }
}
