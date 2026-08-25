package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Ovalchase Dragster
 * {4}
 * Artifact — Vehicle
 * 6/1
 * Trample, haste
 * Crew 1
 *
 * Every line is a printed keyword: [Keyword.TRAMPLE] and [Keyword.HASTE], plus the
 * [KeywordAbility.crew] activated ability the engine owns. Trample and haste only matter once the
 * Vehicle has been crewed into a creature.
 */
val OvalchaseDragster = card("Ovalchase Dragster") {
    manaCost = "{4}"
    typeLine = "Artifact — Vehicle"
    oracleText = "Trample, haste\n" +
        "Crew 1 (Tap any number of creatures you control with total power 1 or more: This Vehicle becomes an artifact creature until end of turn.)"
    power = 6
    toughness = 1

    keywords(Keyword.TRAMPLE, Keyword.HASTE)

    keywordAbility(KeywordAbility.crew(1))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "225"
        artist = "Victor Adame Minguez"
        flavorText = "It'll either crash or win the race. Possibly both."
        imageUri = "https://cards.scryfall.io/normal/front/2/0/20411aa0-f87b-49dd-b943-ca82d59db185.jpg?1783937152"
    }
}
