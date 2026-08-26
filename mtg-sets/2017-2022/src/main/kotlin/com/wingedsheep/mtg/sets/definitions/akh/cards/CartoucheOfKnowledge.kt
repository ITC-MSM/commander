package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Cartouche of Knowledge
 * {1}{U}
 * Enchantment — Aura Cartouche
 * Enchant creature you control
 * When this Aura enters, draw a card.
 * Enchanted creature gets +1/+1 and has flying.
 */
val CartoucheOfKnowledge = card("Cartouche of Knowledge") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura Cartouche"
    oracleText = "Enchant creature you control\n" +
        "When this Aura enters, draw a card.\n" +
        "Enchanted creature gets +1/+1 and has flying."

    auraTarget = Targets.CreatureYouControl

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    staticAbility {
        ability = ModifyStats(1, 1)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "45"
        artist = "Kieran Yanner"
        flavorText = "Cartouches chronicle the initiates' achievements in the trials."
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d806458e-81cd-4413-bba0-14d957bece79.jpg?1783936525"
    }
}
