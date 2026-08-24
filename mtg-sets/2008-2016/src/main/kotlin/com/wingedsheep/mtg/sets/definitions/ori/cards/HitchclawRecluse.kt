package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hitchclaw Recluse
 * {2}{G}
 * Creature — Spider
 * 1/4
 * Reach
 */
val HitchclawRecluse = card("Hitchclaw Recluse") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spider"
    oracleText = "Reach"
    power = 1
    toughness = 4

    keywords(Keyword.REACH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "181"
        artist = "Jeff Simpson"
        flavorText = "Not all spiders need webs to catch their prey."
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bf335eac-e57b-44ec-afe2-8aed567469e7.jpg?1783938322"
    }
}
