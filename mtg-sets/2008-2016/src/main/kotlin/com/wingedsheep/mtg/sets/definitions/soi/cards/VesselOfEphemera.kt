package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Vessel of Ephemera (Shadows over Innistrad #48)
 * {1}{W}
 * Enchantment
 *
 * {2}{W}, Sacrifice this enchantment: Create two 1/1 white Spirit creature tokens with flying.
 */
val VesselOfEphemera = card("Vessel of Ephemera") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "{2}{W}, Sacrifice this enchantment: Create two 1/1 white Spirit creature tokens with flying."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{W}"), Costs.SacrificeSelf)
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Spirit"),
            keywords = setOf(Keyword.FLYING),
            count = 2
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "48"
        artist = "Kieran Yanner"
        flavorText = "Geists seeking redemption must first be given the opportunity."
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f4051020-688c-473a-9a08-b62f0fd75675.jpg?1783937806"
    }
}
