package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Battle Mastery
 * {2}{W}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature has double strike.
 */
val BattleMastery = card("Battle Mastery") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature has double strike. (It deals both " +
        "first-strike and regular combat damage.)"

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantKeyword(Keyword.DOUBLE_STRIKE)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "5"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "\"Boom! Boom! Boots the size of oxcarts, then an axe like a falling sun. Elves scattered. Trees scattered. Even the hills ran for the hills!\"\n—*Clachan Tales*"
        imageUri = "https://cards.scryfall.io/normal/front/7/9/79e234ce-6d61-4d62-a8cd-fa985a6aba60.jpg?1783942918"
    }
}
