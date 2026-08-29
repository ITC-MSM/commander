package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Flight of Fancy
 * {3}{U}
 * Enchantment — Aura
 * Enchant creature
 * When this Aura enters, draw two cards.
 * Enchanted creature has flying.
 */
val FlightOfFancy = card("Flight of Fancy") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "When this Aura enters, draw two cards.\n" +
        "Enchanted creature has flying."

    auraTarget = Targets.Creature

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(2)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "49"
        artist = "Glen Angus"
        flavorText = "The view from above is an inspiration to the newly winged."
        imageUri = "https://cards.scryfall.io/normal/front/7/c/7c8ad201-ce70-45bf-9ac3-51f5ccfa8df9.jpg"
    }
}
