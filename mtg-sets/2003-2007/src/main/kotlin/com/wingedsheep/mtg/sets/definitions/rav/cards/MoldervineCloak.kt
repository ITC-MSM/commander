package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.ModifyStats


/**
 * Moldervine Cloak
 * {2}{G}
 * Enchantment — Aura
 * Enchant creature
 * Enchanted creature gets +3/+3.
 * Dredge 2 (If you would draw a card, you may mill two cards instead. If you do, return this card from your graveyard to your hand.)
 */
val MoldervineCloak = card("Moldervine Cloak") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\nEnchanted creature gets +3/+3.\nDredge 2 (If you would draw a card, you may mill two cards instead. If you do, return this card from your graveyard to your hand.)"
    auraTarget = Targets.Creature
    staticAbility {
        ability = ModifyStats(3, 3)
    }
    keywordAbility(KeywordAbility.dredge(2))
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "173"
        artist = "Wayne England"
        imageUri = "https://cards.scryfall.io/normal/front/8/7/871c3f80-aaa7-485f-b519-57363f5905dd.jpg?1783943634"
    }
}
