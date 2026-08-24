package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Divine Favor
 * {1}{W}
 * Enchantment — Aura
 * Enchant creature
 * When this Aura enters, you gain 3 life.
 * Enchanted creature gets +1/+3.
 *
 * Canonical printing: Magic 2012, the card's earliest real-expansion printing. Reprinted in M13,
 * M14 and M15 as `Printing` rows.
 */
val DivineFavor = card("Divine Favor") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText =
        "Enchant creature\n" +
        "When this Aura enters, you gain 3 life.\n" +
        "Enchanted creature gets +1/+3."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(3)
        description = "When this Aura enters, you gain 3 life."
    }

    staticAbility {
        ability = ModifyStats(+1, +3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "14"
        artist = "Allen Williams"
        flavorText = "With an armory of light, even the squire may champion her people."
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1f44e053-95c2-410f-b35d-8ea3e3607e82.jpg?1783941103"
    }
}
