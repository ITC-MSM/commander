package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Needlethorn Drake — Strixhaven: School of Mages #208 (canonical printing)
 * {G}{U} · Creature — Drake · 1/1
 *
 * Flying, deathtouch
 *
 * Two plain evergreen keywords, nothing else — both are simple [Keyword] markers the engine
 * reads directly.
 */
val NeedlethornDrake = card("Needlethorn Drake") {
    manaCost = "{G}{U}"
    colorIdentity = "GU"
    typeLine = "Creature — Drake"
    oracleText =
        "Flying, deathtouch"
    power = 1
    toughness = 1

    keywords(Keyword.FLYING, Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "208"
        artist = "Donato Giancola"
        flavorText = "As they learn to fly, young drakes have a tendency to accidentally impale trees, cliffs, and unsuspecting students."
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c0cf2c4-723e-46c4-b2aa-4c957177209a.jpg?1783927304"
    }
}
