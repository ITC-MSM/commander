package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Tunnel Surveyor
 * {2}{U}
 * Creature — Human Detective
 * 2/2
 *
 * When this creature enters, create a 1/1 white Glimmer enchantment creature token.
 */
val TunnelSurveyor = card("Tunnel Surveyor") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Detective"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, create a 1/1 white Glimmer enchantment creature token."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Glimmer"),
            enchantmentToken = true,
            imageUri = "https://cards.scryfall.io/normal/front/4/7/475c7449-2c95-4873-94de-68a5e06cdfb8.jpg?1754930946",
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "76"
        artist = "John Stanko"
        flavorText = "Though he needed to know what lay beyond the intersection, Harp couldn't suppress a shiver " +
            "of disquiet as his glimmer disappeared into the darkness ahead."
        imageUri = "https://cards.scryfall.io/normal/front/1/2/12ee2690-f464-4a38-81e1-6dd2053a3327.jpg?1726286136"
    }
}
