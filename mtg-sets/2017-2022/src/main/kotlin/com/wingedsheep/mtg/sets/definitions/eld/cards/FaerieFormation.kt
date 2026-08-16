package com.wingedsheep.mtg.sets.definitions.eld.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Faerie Formation
 * {4}{U}
 * Creature — Faerie
 * 5/4
 * Flying
 * {3}{U}: Create a 1/1 blue Faerie creature token with flying. Draw a card.
 */
val FaerieFormation = card("Faerie Formation") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Faerie"
    power = 5
    toughness = 4
    oracleText = "Flying\n{3}{U}: Create a 1/1 blue Faerie creature token with flying. Draw a card."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{3}{U}")
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLUE),
            creatureTypes = setOf("Faerie"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/d/1/d1c0556e-ba3c-4a8e-b704-8eaa7c4dba1c.jpg?1782727481",
        ).then(Effects.DrawCards(1))
        description = "{3}{U}: Create a 1/1 blue Faerie creature token with flying. Draw a card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "316"
        artist = "Ryan Yee"
        flavorText = "The throng flitted from castle to castle, leaving a trail of star-crossed love, damaging rumors, and missing heirlooms in their wake."
        imageUri = "https://cards.scryfall.io/normal/front/1/5/15709316-7382-46b9-9b70-53a5147e7051.jpg?1783932552"
    }
}
