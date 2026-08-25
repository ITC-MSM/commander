package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Ambitious Aetherborn
 * {4}{B}
 * Creature — Aetherborn Artificer
 * 4/3
 * Fabricate 1 (When this creature enters, put a +1/+1 counter on it or create a 1/1 colorless Servo
 * artifact creature token.)
 *
 * Fabricate is a printed keyword ability — [KeywordAbility.fabricate] and nothing else. The engine
 * derives the enters-the-battlefield choice from it.
 */
val AmbitiousAetherborn = card("Ambitious Aetherborn") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Aetherborn Artificer"
    oracleText = "Fabricate 1 (When this creature enters, put a +1/+1 counter on it or create a 1/1 colorless Servo artifact creature token.)"
    power = 4
    toughness = 3

    keywordAbility(KeywordAbility.fabricate(1))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "72"
        artist = "Josu Hernaiz"
        flavorText = "Aetherborn who are unwilling to accept the decomposition of their bodies invent ways to preserve and augment themselves."
        imageUri = "https://cards.scryfall.io/normal/front/8/0/80cb628e-fa83-4d7e-92cb-8779ea02193f.jpg?1783937211"
    }
}
