package com.wingedsheep.mtg.sets.definitions.ptk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shu Elite Infantry
 * {3}{W}
 * Creature — Human Soldier
 * 3/3
 *
 * Vanilla — no rules text.
 */
val ShuEliteInfantry = card("Shu Elite Infantry") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 3
    toughness = 3

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "22"
        artist = "Song Shikai"
        flavorText = "Kongming's first campaign against the Wei kingdom was a rousing success until an arrogant Shu general, Ma Su, foolishly lost the city of Jieting."
        imageUri = "https://cards.scryfall.io/normal/front/3/6/36bcd751-1142-4e72-9d87-7a25c74c038b.jpg?1783946128"
    }
}
