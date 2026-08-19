package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Carnivorous Plant
 * {3}{G}
 * Creature — Plant Wall
 * 4/5
 *
 * Defender — the modern Oracle wording for the original "Wall" text; the Wall creature type is
 * on the type line and no longer carries the can't-attack rule itself (CR 702.3).
 */
val CarnivorousPlant = card("Carnivorous Plant") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant Wall"
    power = 4
    toughness = 5
    oracleText = "Defender"

    keywords(Keyword.DEFENDER)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "75"
        artist = "Quinton Hoover"
        flavorText = "\"It had a mouth like that of a great beast, and gnashed its teeth as it strained to reach us. I am thankful it possessed no means of locomotion.\" —Vervamon the Elder"
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6a615650-4da3-4efc-aa5e-c1f2c4f79478.jpg?1783947933"
    }
}
