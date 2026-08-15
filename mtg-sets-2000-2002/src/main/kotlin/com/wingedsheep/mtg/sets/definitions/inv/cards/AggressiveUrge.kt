package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Aggressive Urge
 * {1}{G}
 * Instant
 * Target creature gets +1/+1 until end of turn.
 * Draw a card.
 */
val AggressiveUrge = card("Aggressive Urge") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature gets +1/+1 until end of turn.\nDraw a card."

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.ModifyStats(power = 1, toughness = 1, target = t)
            .then(Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "181"
        artist = "Christopher Moeller"
        flavorText = "The power of the wild, concentrated in a single charge."
        imageUri = "https://cards.scryfall.io/normal/front/3/7/37e3154d-9b1c-4f93-9bc3-a39e68d59d23.jpg?1562906253"
    }
}
