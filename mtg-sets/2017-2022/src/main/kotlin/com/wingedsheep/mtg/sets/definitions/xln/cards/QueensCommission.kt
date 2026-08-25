package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Queen's Commission
 * {2}{W}
 * Sorcery
 *
 * Create two 1/1 white Vampire creature tokens with lifelink.
 */
val QueensCommission = card("Queen's Commission") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Create two 1/1 white Vampire creature tokens with lifelink."

    spell {
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Vampire"),
            keywords = setOf(Keyword.LIFELINK),
            count = 2,
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "29"
        artist = "Mark Behm"
        flavorText = "\"Let the blood of the impure flow through you. Only the blessings of the golden city will purge its acrid taste from your mouth.\"\n—High Marshal Arguel"
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f06c0007-299e-4d71-99c3-f905d942759d.jpg"
    }
}
