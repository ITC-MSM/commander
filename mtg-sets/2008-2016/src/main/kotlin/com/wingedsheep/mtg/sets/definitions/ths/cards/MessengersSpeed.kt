package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Messenger's Speed
 * {R}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature has trample and haste.
 */
val MessengersSpeed = card("Messenger's Speed") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature has trample and haste."

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantKeyword(Keyword.TRAMPLE)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.HASTE)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "129"
        artist = "Clint Cearley"
        flavorText = "\"He outran arrows. He outran even the archers' insults.\"\n—Bayma, storyteller of Lagonna Band"
        imageUri = "https://cards.scryfall.io/normal/front/9/3/93d7f033-217f-4fb3-a57b-e32291257ec8.jpg"
    }
}
