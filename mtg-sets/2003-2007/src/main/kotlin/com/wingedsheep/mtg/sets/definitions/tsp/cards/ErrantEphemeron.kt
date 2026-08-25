package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Errant Ephemeron
 * {6}{U}
 * Creature — Illusion
 * 4 / 4
 * Flying
 * Suspend 4—{1}{U} (Rather than cast this card from your hand, you may pay {1}{U} and exile it with four time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost. It has haste.)
 *
 * Two printed keywords, nothing else. Flying is a real engine keyword; suspend is the
 * parameterized [KeywordAbility.Suspend] — cost first, time counters second.
 */
val ErrantEphemeron = card("Errant Ephemeron") {
    manaCost = "{6}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Illusion"
    power = 4
    toughness = 4
    oracleText = "Flying\n" +
        "Suspend 4—{1}{U} (Rather than cast this card from your hand, you may pay {1}{U} and exile it with four time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost. It has haste.)"

    keywords(Keyword.FLYING)

    keywordAbility(KeywordAbility.suspend("{1}{U}", 4))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "60"
        artist = "Luca Zontini"
        imageUri = "https://cards.scryfall.io/normal/front/3/9/398c26cc-cd55-42c6-a744-aaefd7018960.jpg"
    }
}
