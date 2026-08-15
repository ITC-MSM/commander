package com.wingedsheep.mtg.sets.definitions.oana.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shrine Keeper
 * {W}{W}
 * Creature — Human Cleric
 * 2/2
 *
 * Vanilla — no rules text.
 */
val ShrineKeeper = card("Shrine Keeper") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    power = 2
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "10"
        artist = "Craig J Spearing"
        imageUri = "https://cards.scryfall.io/normal/front/7/4/74f04961-c42e-41fb-a770-62c7d3d2b83a.jpg?1783934410"
    }
}
