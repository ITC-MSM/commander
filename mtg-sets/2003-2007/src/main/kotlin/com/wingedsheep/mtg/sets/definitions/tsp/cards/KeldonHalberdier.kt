package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Keldon Halberdier
 * {4}{R}
 * Creature — Human Warrior
 * 4 / 1
 * First strike
 * Suspend 4—{R} (Rather than cast this card from your hand, you may pay {R} and exile it with four time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost. It has haste.)
 *
 * Two printed keywords, nothing else — first strike is a real engine keyword, suspend is the
 * parameterized [KeywordAbility.Suspend].
 */
val KeldonHalberdier = card("Keldon Halberdier") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Warrior"
    power = 4
    toughness = 1
    oracleText = "First strike\n" +
        "Suspend 4—{R} (Rather than cast this card from your hand, you may pay {R} and exile it with four time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost. It has haste.)"

    keywords(Keyword.FIRST_STRIKE)

    keywordAbility(KeywordAbility.suspend("{R}", 4))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "167"
        artist = "Paolo Parente"
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a90723e0-fbb3-4976-9463-0373f8ed337c.jpg"
    }
}
