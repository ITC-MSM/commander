package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Hill Gigas
 * {4}{R}{R}
 * Creature — Giant
 * 5/4
 *
 * Trample, haste
 * Mountaincycling {2} ({2}, Discard this card: Search your library for a Mountain card, reveal it,
 * put it into your hand, then shuffle.)
 */
val HillGigas = card("Hill Gigas") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Giant"
    power = 5
    toughness = 4
    oracleText = "Trample, haste\nMountaincycling {2} ({2}, Discard this card: Search your library " +
        "for a Mountain card, reveal it, put it into your hand, then shuffle.)"

    keywords(Keyword.TRAMPLE, Keyword.HASTE)

    keywordAbility(KeywordAbility.typecycling("Mountain", ManaCost.parse("{2}")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "141"
        artist = "Heonhwa"
        flavorText = "\"This place is dangerous! Be careful!\"\n—Zozo resident"
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2a8b3e1e-5c10-4360-ac1c-83b2e026278c.jpg?1748706292"
    }
}
