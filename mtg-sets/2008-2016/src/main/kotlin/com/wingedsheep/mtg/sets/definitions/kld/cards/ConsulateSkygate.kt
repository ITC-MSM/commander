package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Consulate Skygate
 * {2}
 * Artifact Creature — Wall
 * 0/4
 * Defender
 * Reach
 *
 * Two parameterless keywords and nothing else — both are engine-live, so `keywords(...)` is the
 * whole implementation.
 */
val ConsulateSkygate = card("Consulate Skygate") {
    manaCost = "{2}"
    typeLine = "Artifact Creature — Wall"
    oracleText = "Defender\n" +
        "Reach (This creature can block creatures with flying.)"
    power = 0
    toughness = 4

    keywords(Keyword.DEFENDER, Keyword.REACH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "202"
        artist = "John Avon"
        flavorText = "All skyships entering or leaving the fairgrounds must pass through the security checkpoint."
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9a7922c3-1baa-41ed-bd06-b5a97cddb90e.jpg?1783937162"
    }
}
