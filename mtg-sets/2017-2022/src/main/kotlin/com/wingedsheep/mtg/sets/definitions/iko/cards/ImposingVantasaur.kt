package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Imposing Vantasaur
 * {5}{W}
 * Creature — Dinosaur
 * 3/6
 * Vigilance
 * Cycling {1} ({1}, Discard this card: Draw a card.)
 */
val ImposingVantasaur = card("Imposing Vantasaur") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dinosaur"
    power = 3
    toughness = 6
    oracleText = "Vigilance\nCycling {1} ({1}, Discard this card: Draw a card.)"

    keywords(Keyword.VIGILANCE)
    keywordAbility(KeywordAbility.cycling("{1}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "17"
        artist = "Jonathan Kuo"
        flavorText = "Raugrin's coastline can be quite beautiful, as long as you avoid the volcanic dinosaurs, the sea dinosaurs, and the roaming beach dinosaurs."
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b6fa5feb-f5e9-4079-acc9-84e458044769.jpg"
    }
}
