package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Glimmerburst
 * {3}{U}
 * Instant
 *
 * Draw two cards. Create a 1/1 white Glimmer enchantment creature token.
 */
val Glimmerburst = card("Glimmerburst") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Draw two cards. Create a 1/1 white Glimmer enchantment creature token."

    spell {
        effect = Effects.DrawCards(2) then Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Glimmer"),
            enchantmentToken = true,
            imageUri = "https://cards.scryfall.io/normal/front/4/7/475c7449-2c95-4873-94de-68a5e06cdfb8.jpg?1754930946"
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "62"
        artist = "Dan Watson"
        flavorText = "At first Sasha felt silly fending off the cellarspawn hordes with the essence of her childhood pet hamster. But it sure beat dying alone in the dark."
        imageUri = "https://cards.scryfall.io/normal/front/f/e/fe1ab8db-3994-4b6d-9eaf-75243a78b715.jpg?1726286087"
    }
}
