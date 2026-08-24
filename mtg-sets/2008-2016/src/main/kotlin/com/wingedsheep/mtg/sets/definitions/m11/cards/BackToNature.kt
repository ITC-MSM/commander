package com.wingedsheep.mtg.sets.definitions.m11.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Back to Nature
 * {1}{G}
 * Instant
 * Destroy all enchantments.
 *
 * Canonical printing: Magic 2011, the card's earliest real-expansion printing. Reprinted in M15
 * as a `Printing` row.
 */
val BackToNature = card("Back to Nature") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Destroy all enchantments."

    spell {
        effect = Patterns.Group.destroyAllPipeline(GameObjectFilter.Enchantment)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "164"
        artist = "Howard Lyon"
        flavorText = "\"Nature is a mutable cloud which is always and never the same.\"\n—Ralph Waldo Emerson, *Essays*"
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e7af9ebf-8935-4107-aacd-4c643b68cb6e.jpg?1783941801"
    }
}
