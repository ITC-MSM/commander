package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Visionary Augmenter
 * {2}{W}{W}
 * Creature — Dwarf Artificer
 * 2 / 1
 *
 * Fabricate 2 (When this creature enters, put two +1/+1 counters on it or create two 1/1 colorless
 * Servo artifact creature tokens.)
 *
 * Fabricate is a keyword ability — the engine derives the modal enters trigger from
 * [KeywordAbility.fabricate], so the card carries the keyword and nothing else.
 */
val VisionaryAugmenter = card("Visionary Augmenter") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dwarf Artificer"
    oracleText = "Fabricate 2 (When this creature enters, put two +1/+1 counters on it or create two 1/1 colorless Servo artifact creature tokens.)"
    power = 2
    toughness = 1

    keywordAbility(KeywordAbility.fabricate(2))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "34"
        artist = "James Paick"
        flavorText = "\"It's a multifunctional radial extension system with superlateral, force-amplifying rigging. Of course it will win.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/1/918691b1-f927-4027-a444-adc418f3ab16.jpg?1783937226"
    }
}
