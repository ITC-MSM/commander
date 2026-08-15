package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ControlEnchantedPermanent

/**
 * In Bolas's Clutches
 * {4}{U}{U}
 * Legendary Enchantment — Aura
 * Enchant permanent
 * You control enchanted permanent.
 * Enchanted permanent is legendary.
 *
 * Note: The "enchanted permanent is legendary" supertype addition is not yet implemented.
 * This only affects the legend rule and is a rare edge case.
 */
val InBolassClutches = card("In Bolas's Clutches") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Enchantment — Aura"
    oracleText = "Enchant permanent\nYou control enchanted permanent.\nEnchanted permanent is legendary."

    auraTarget = Targets.Permanent

    staticAbility {
        ability = ControlEnchantedPermanent
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "54"
        artist = "Zack Stella"
        flavorText = "\"Your contract is in default. You belong to me now. Serve, or die.\"\n—Nicol Bolas"
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d72bf6b4-ac70-4be0-86de-cb3c47244dbc.jpg?1562743727"
    }
}
