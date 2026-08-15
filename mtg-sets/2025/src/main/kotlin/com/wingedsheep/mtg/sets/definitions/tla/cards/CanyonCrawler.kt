package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Canyon Crawler
 * {4}{B}{B}
 * Creature — Spider Beast
 * 6/6
 * Deathtouch
 * When this creature enters, create a Food token. (It's an artifact with
 * "{2}, {T}, Sacrifice this token: You gain 3 life.")
 * Swampcycling {2} ({2}, Discard this card: Search your library for a Swamp card,
 * reveal it, put it into your hand, then shuffle.)
 *
 * Swampcycling is Typecycling for the Swamp land type ([KeywordAbility.typecycling]).
 */
val CanyonCrawler = card("Canyon Crawler") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spider Beast"
    oracleText = "Deathtouch\n" +
        "When this creature enters, create a Food token. (It's an artifact with \"{2}, {T}, " +
        "Sacrifice this token: You gain 3 life.\")\n" +
        "Swampcycling {2} ({2}, Discard this card: Search your library for a Swamp card, reveal it, " +
        "put it into your hand, then shuffle.)"
    power = 6
    toughness = 6

    keywords(Keyword.DEATHTOUCH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateFood()
    }

    keywordAbility(KeywordAbility.typecycling("Swamp", ManaCost.parse("{2}")))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "90"
        artist = "Kohei Hayama"
        imageUri = "https://cards.scryfall.io/normal/front/3/0/30a30bfb-f0f3-425b-b37d-20079ee27046.jpg?1764120621"
    }
}
