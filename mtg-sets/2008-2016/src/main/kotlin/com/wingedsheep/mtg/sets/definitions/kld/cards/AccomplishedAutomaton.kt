package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Accomplished Automaton
 * {7}
 * Artifact Creature — Construct
 * 5/7
 * Fabricate 1 (When this creature enters, put a +1/+1 counter on it or create a 1/1 colorless Servo
 * artifact creature token.)
 *
 * Fabricate is a printed keyword ability — [KeywordAbility.fabricate] and nothing else. The engine
 * derives the enters-the-battlefield choice from it, so the card never spells the modal trigger out.
 */
val AccomplishedAutomaton = card("Accomplished Automaton") {
    manaCost = "{7}"
    typeLine = "Artifact Creature — Construct"
    oracleText = "Fabricate 1 (When this creature enters, put a +1/+1 counter on it or create a 1/1 colorless Servo artifact creature token.)"
    power = 5
    toughness = 7

    keywordAbility(KeywordAbility.fabricate(1))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "191"
        artist = "Daarken"
        flavorText = "Was it just lonely, or was some form of evolution inevitable?"
        imageUri = "https://cards.scryfall.io/normal/front/6/1/61dc2362-f901-4ec6-9bc4-1988f30380fd.jpg?1783937165"
    }
}
