package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sworn Companions
 * {2}{W}
 * Sorcery
 * Create two 1/1 white Soldier creature tokens with lifelink.
 */
val SwornCompanions = card("Sworn Companions") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Create two 1/1 white Soldier creature tokens with lifelink."

    spell {
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Soldier"),
            keywords = setOf(Keyword.LIFELINK),
            count = 2
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "27"
        artist = "Jason Rainville"
        flavorText = "\"The trouble with youths these days is that, in outright defiance of their elders, they refuse to be bought.\"\n—Karlov of the Ghost Council"
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2ccfa5c1-69f9-4351-aba3-883fe92c9b98.jpg?1783934194"
    }
}
