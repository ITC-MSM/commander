package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Daggermaw Megalodon
 * {4}{U}{U}
 * Creature — Shark
 * 5/7
 * Vigilance
 * Islandcycling {2} ({2}, Discard this card: Search your library for an Island card,
 * reveal it, put it into your hand, then shuffle.)
 */
val DaggermawMegalodon = card("Daggermaw Megalodon") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Shark"
    power = 5
    toughness = 7
    oracleText = "Vigilance\n" +
        "Islandcycling {2} ({2}, Discard this card: Search your library for an Island card, " +
        "reveal it, put it into your hand, then shuffle.)"

    keywords(Keyword.VIGILANCE)
    keywordAbility(KeywordAbility.typecycling("Island", ManaCost.parse("{2}")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "48"
        artist = "Helge C. Balzer"
        flavorText = "Valgavoth's evil aura turned the shark from an apex predator into an inescapable terror of the deeps."
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e72a38b9-29aa-4804-ab27-4c40321f0bc3.jpg?1726286036"
    }
}
