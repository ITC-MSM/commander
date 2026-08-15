package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ReplaceLandManaColor

/**
 * Pulse of Llanowar
 * {3}{G}
 * Enchantment
 * If a basic land you control is tapped for mana, it produces mana of a color of your choice instead
 * of any other type.
 *
 * Invasion engine gap #3. The new [ReplaceLandManaColor] static makes a matched land's mana ability
 * produce one mana of a color of its controller's choice instead of its normal output. The engine
 * implements this by swapping the land's base mana effect for an "add one mana of any color" effect,
 * so the choice flows through the existing any-color machinery (a manual tap prompts for the color;
 * the mana solver treats a matched basic as a five-color source when auto-tapping for a cost).
 */
val PulseOfLlanowar = card("Pulse of Llanowar") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "If a basic land you control is tapped for mana, it produces mana of a color of your choice instead of any other type."

    staticAbility {
        ability = ReplaceLandManaColor(filter = GameObjectFilter.BasicLand.youControl())
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "202"
        artist = "Rebecca Guay"
        flavorText = "\"Gaea's memory is eternal, and she exists in all things.\"\n—Molimo, maro-sorcerer"
        imageUri = "https://cards.scryfall.io/normal/front/d/b/db09afe5-5f01-4f77-a239-12d7a6e59024.jpg?1562939018"
    }
}
