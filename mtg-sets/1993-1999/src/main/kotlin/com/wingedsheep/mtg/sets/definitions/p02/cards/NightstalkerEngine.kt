package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Nightstalker Engine
 * {4}{B}
 * Creature — Nightstalker
 *
 * Nightstalker Engine's power is equal to the number of creature cards in your graveyard.
 *
 * A characteristic-defining ability (CR 604.3): the starred power lives in the P/T slot, so it is a
 * `dynamicPower(...)` over a graveyard count rather than an entry in a `CardScript`. Toughness stays
 * a printed 3.
 */
val NightstalkerEngine = card("Nightstalker Engine") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Nightstalker"
    oracleText = "Nightstalker Engine's power is equal to the number of creature cards in your graveyard."
    toughness = 3

    dynamicPower(DynamicAmounts.creatureCardsInYourGraveyard())

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "81"
        artist = "Mark Tedin"
        flavorText = "Part metal, part bone, and all death."
        imageUri = "https://cards.scryfall.io/normal/front/6/1/61e440ad-462f-4c7e-a646-8c5123a9f6b2.jpg"
    }
}
