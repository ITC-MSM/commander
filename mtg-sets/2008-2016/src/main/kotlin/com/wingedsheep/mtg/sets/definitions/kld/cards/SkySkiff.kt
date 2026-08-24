package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Sky Skiff
 * {2}
 * Artifact — Vehicle
 * 2/3
 * Flying
 * Crew 1 (Tap any number of creatures you control with total power 1 or more: This Vehicle becomes
 * an artifact creature until end of turn.)
 *
 * Both lines are printed keywords, the Aradara Express shape: [Keyword.FLYING] plus the engine's
 * [KeywordAbility.crew] activated ability. Flying only matters once the Vehicle has been crewed
 * into a creature.
 */
val SkySkiff = card("Sky Skiff") {
    manaCost = "{2}"
    typeLine = "Artifact — Vehicle"
    oracleText = "Flying\n" +
        "Crew 1 (Tap any number of creatures you control with total power 1 or more: This Vehicle becomes an artifact creature until end of turn.)"
    power = 2
    toughness = 3

    keywords(Keyword.FLYING)

    keywordAbility(KeywordAbility.crew(1))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "233"
        artist = "Richard Wright"
        imageUri = "https://cards.scryfall.io/normal/front/c/f/cf4a4939-130b-40d7-8a0f-e31eb931d2d5.jpg?1783937149"
    }
}
