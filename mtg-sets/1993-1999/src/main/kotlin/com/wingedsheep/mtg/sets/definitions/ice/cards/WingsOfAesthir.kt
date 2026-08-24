package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Wings of Aesthir
 * {W}{U}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature gets +1/+0 and has flying and first strike.
 *
 * The Serra's Embrace shape: one `ModifyStats` plus one `GrantKeyword` per printed keyword, each
 * left on its default `attachedCreature()` filter so they all read "enchanted creature". Two
 * separate grants rather than a combined one because the SDK models a keyword grant per keyword.
 */
val WingsOfAesthir = card("Wings of Aesthir") {
    manaCost = "{W}{U}"
    colorIdentity = "UW"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature gets +1/+0 and has flying and first strike."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(1, 0)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FLYING)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.FIRST_STRIKE)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "305"
        artist = "Edward P. Beard, Jr."
        flavorText = "\"For those of courage, even the sky holds no limit.\"\n—Arnjlot Olasson, Sky Mage"
        imageUri = "https://cards.scryfall.io/normal/front/e/e/eeb0282d-ccec-4556-8b70-b6f665077afe.jpg"
    }
}
