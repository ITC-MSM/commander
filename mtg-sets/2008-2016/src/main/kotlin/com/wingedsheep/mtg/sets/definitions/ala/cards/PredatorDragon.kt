package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDevour
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Predator Dragon
 * {3}{R}{R}{R}
 * Creature — Dragon
 * 4 / 4
 * Flying, haste
 * Devour 2 (As this creature enters, you may sacrifice any number of creatures. It enters with twice that many +1/+1 counters on it.)
 *
 * Devour is two halves that must both be authored: [KeywordAbility.devour] is the printed keyword
 * (it carries the multiplier and renders the reminder text), and [EntersWithDevour] is the
 * replacement effect that actually does the work as the permanent enters — offering the sacrifice
 * and multiplying the count into +1/+1 counters. Its defaults are already this card's variant
 * (a creature sacrifice filter, no variant word), so only the multiplier is passed.
 */
val PredatorDragon = card("Predator Dragon") {
    manaCost = "{3}{R}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dragon"
    power = 4
    toughness = 4
    oracleText = "Flying, haste\n" +
        "Devour 2 (As this creature enters, you may sacrifice any number of creatures. It enters with twice that many +1/+1 counters on it.)"

    keywords(Keyword.FLYING, Keyword.HASTE, Keyword.DEVOUR)
    keywordAbility(KeywordAbility.devour(2))

    replacementEffect(EntersWithDevour(multiplier = 2))

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "109"
        artist = "Raymond Swanland"
        flavorText = "Dragons make for spiteful gods."
        imageUri = "https://cards.scryfall.io/normal/front/3/5/352c3c86-1662-4055-b814-026826632cff.jpg"
    }
}
