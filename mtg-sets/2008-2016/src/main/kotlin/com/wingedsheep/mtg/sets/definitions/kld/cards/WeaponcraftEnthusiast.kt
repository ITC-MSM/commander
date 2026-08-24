package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Weaponcraft Enthusiast
 * {2}{B}
 * Creature — Aetherborn Artificer
 * 0 / 1
 *
 * Fabricate 2 (When this creature enters, put two +1/+1 counters on it or create two 1/1 colorless
 * Servo artifact creature tokens.)
 *
 * Fabricate is a keyword ability — the engine derives the modal enters trigger from
 * [KeywordAbility.fabricate], so the card carries the keyword and nothing else.
 */
val WeaponcraftEnthusiast = card("Weaponcraft Enthusiast") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Aetherborn Artificer"
    oracleText = "Fabricate 2 (When this creature enters, put two +1/+1 counters on it or create two 1/1 colorless Servo artifact creature tokens.)"
    power = 0
    toughness = 1

    keywordAbility(KeywordAbility.fabricate(2))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "105"
        artist = "Mike Bierek"
        flavorText = "\"No, I don't believe they have the proper permits for those, but I'm not going to be the one to ask.\"\n—Caru, Consulate warden"
        imageUri = "https://cards.scryfall.io/normal/front/f/e/fe99535a-cc81-4e79-9d30-d514c86b849c.jpg?1783937199"
    }
}
