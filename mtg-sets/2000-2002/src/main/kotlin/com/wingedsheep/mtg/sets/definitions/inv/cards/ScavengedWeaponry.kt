package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Scavenged Weaponry
 * {2}{B}
 * Enchantment — Aura
 * Enchant creature
 * When this Aura enters, draw a card.
 * Enchanted creature gets +1/+1.
 */
val ScavengedWeaponry = card("Scavenged Weaponry") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "When this Aura enters, draw a card.\n" +
        "Enchanted creature gets +1/+1."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    staticAbility {
        ability = ModifyStats(1, 1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "123"
        artist = "Alan Pollack"
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4e8072a9-2699-4c6c-9556-67d91bd67a4b.jpg?1562910890"
    }
}
