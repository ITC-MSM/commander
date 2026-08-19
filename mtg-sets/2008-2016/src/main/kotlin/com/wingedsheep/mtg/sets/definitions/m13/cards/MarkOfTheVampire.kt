package com.wingedsheep.mtg.sets.definitions.m13.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Mark of the Vampire
 * {3}{B}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature gets +2/+2 and has lifelink.
 */
val MarkOfTheVampire = card("Mark of the Vampire") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature gets +2/+2 and has lifelink."

    auraTarget = Targets.Creature

    staticAbility {
        ability = ModifyStats(2, 2)
    }

    staticAbility {
        ability = GrantKeyword(Keyword.LIFELINK)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "99"
        artist = "Winona Nelson"
        flavorText = "\"My 'condition' is a trial. The weak are consumed by it. The strong transcend it.\"\n—Sorin Markov"
        imageUri = "https://cards.scryfall.io/normal/front/9/0/90484815-2529-4a81-9f1b-f0f7382e4b66.jpg?1783940493"
    }
}
