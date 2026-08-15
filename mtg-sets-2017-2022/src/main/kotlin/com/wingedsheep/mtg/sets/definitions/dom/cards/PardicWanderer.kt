package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pardic Wanderer
 * {6}
 * Artifact Creature — Golem
 * 5/5
 * Trample
 */
val PardicWanderer = card("Pardic Wanderer") {
    manaCost = "{6}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Golem"
    power = 5
    toughness = 5
    oracleText = "Trample"

    keywords(Keyword.TRAMPLE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "226"
        artist = "Igor Kieryluk"
        flavorText = "\"To the head of Archaeological Findings: The excavation schedule at dig site 93-beta must be revised. Part of the site has walked off.\" —Tolarian field dispatch"
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2fffe967-3a99-4f00-af46-f4e5567598df.jpg?1562733512"
    }
}
