package com.wingedsheep.mtg.sets.definitions.rtr.cards

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
 * Knightly Valor
 * {4}{W}
 * Enchantment — Aura
 *
 * Enchant creature
 * When this Aura enters, create a 2/2 white Knight creature token with vigilance. (Attacking doesn't cause it to tap.)
 * Enchanted creature gets +2/+2 and has vigilance.
 */
val KnightlyValor = card("Knightly Valor") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nWhen this Aura enters, create a 2/2 white Knight creature token with vigilance. (Attacking doesn't cause it to tap.)\nEnchanted creature gets +2/+2 and has vigilance."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Knight"),
            keywords = setOf(Keyword.VIGILANCE),
        )
        description = "When this Aura enters, create a 2/2 white Knight creature token with vigilance."
    }

    staticAbility {
        ability = ModifyStats(2, 2)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.VIGILANCE)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "13"
        artist = "Matt Stewart"
        imageUri = "https://cards.scryfall.io/normal/front/1/2/122d821f-c8dd-4a3c-a6d7-b42fe5491f02.jpg?1783940374"
    }
}
