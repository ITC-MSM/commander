package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Iron League Steed
 * {4}
 * Artifact Creature — Construct
 * 2/2
 * Haste
 * Fabricate 1 (When this creature enters, put a +1/+1 counter on it or create a 1/1 colorless
 * Servo artifact creature token.)
 *
 * Fabricate is a keyword ability (CR 702.122) — the engine derives its enters-the-battlefield
 * modal trigger from [KeywordAbility.fabricate], so the card carries nothing but the keywords.
 */
val IronLeagueSteed = card("Iron League Steed") {
    manaCost = "{4}"
    typeLine = "Artifact Creature — Construct"
    oracleText = "Haste\n" +
        "Fabricate 1 (When this creature enters, put a +1/+1 counter on it or create a 1/1 colorless Servo artifact creature token.)"
    power = 2
    toughness = 2
    keywords(Keyword.HASTE)

    keywordAbility(KeywordAbility.fabricate(1))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "219"
        artist = "Darek Zabrocki"
        flavorText = "The Iron League prides itself on its metal-shaping techniques."
        imageUri = "https://cards.scryfall.io/normal/front/c/4/c47993b2-694d-4697-8b06-64aa5663598b.jpg?1783937154"
    }
}
