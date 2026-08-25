package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Glint-Sleeve Artisan
 * {2}{W}
 * Creature — Dwarf Artificer
 * 2/2
 * Fabricate 1 (When this creature enters, put a +1/+1 counter on it or create a 1/1 colorless
 * Servo artifact creature token.)
 *
 * Fabricate is a keyword ability (CR 702.122) — the engine derives its enters-the-battlefield
 * modal trigger from [KeywordAbility.fabricate], so the card carries nothing but the keyword.
 */
val GlintSleeveArtisan = card("Glint-Sleeve Artisan") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dwarf Artificer"
    oracleText = "Fabricate 1 (When this creature enters, put a +1/+1 counter on it or create a 1/1 colorless Servo artifact creature token.)"
    power = 2
    toughness = 2

    keywordAbility(KeywordAbility.fabricate(1))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "17"
        artist = "Ryan Pancoast"
        flavorText = "\"Shine bright, bolts tight.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7e39e79b-2755-4fb4-86b5-b6e350ce9514.jpg?1783937232"
    }
}
