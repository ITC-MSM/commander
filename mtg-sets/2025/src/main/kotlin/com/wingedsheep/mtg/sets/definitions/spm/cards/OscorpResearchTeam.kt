package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect

/**
 * Oscorp Research Team
 * {3}{U}
 * Creature — Human Scientist
 * 1/5
 * {6}{U}: Draw two cards.
 */
val OscorpResearchTeam = card("Oscorp Research Team") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Scientist"
    oracleText = "{6}{U}: Draw two cards."
    power = 1
    toughness = 5
    activatedAbility {
        cost = Costs.Mana("{6}{U}")
        effect = DrawCardsEffect(2)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "40"
        artist = "Gal Or"
        flavorText = "Norman Osborn relentlessly pursues success, and he demands everyone in his employ do the same."
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a800ffb4-0c48-41eb-b221-cf1d855131d9.jpg?1757376999"
    }
}
