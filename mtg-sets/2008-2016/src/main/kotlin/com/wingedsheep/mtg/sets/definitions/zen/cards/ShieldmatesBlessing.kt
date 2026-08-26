package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shieldmate's Blessing
 * {W}
 * Instant
 * Prevent the next 3 damage that would be dealt to any target this turn.
 */
val ShieldmatesBlessing = card("Shieldmate's Blessing") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Prevent the next 3 damage that would be dealt to any target this turn."

    spell {
        val anyTarget = target("any target", Targets.Any)
        effect = Effects.PreventNextDamage(3, anyTarget)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "35"
        artist = "Mike Bierek"
        flavorText = "\"Even land dwellers may call for Emeria's grace in times of need.\"\n—Emeria's Creed"
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f58a26f8-a2c9-48e5-8662-7cbd43c00411.jpg"
    }
}
