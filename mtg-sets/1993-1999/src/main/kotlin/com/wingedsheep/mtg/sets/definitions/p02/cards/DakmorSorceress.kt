package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Dakmor Sorceress
 * {5}{B}
 * Creature — Human Wizard Sorcerer
 *
 * Dakmor Sorceress's power is equal to the number of Swamps you control.
 *
 * A characteristic-defining ability (CR 604.3): the starred power is the P/T slot itself, not an
 * entry in the ability list, so it lives in the card's dynamic power rather than inside a
 * `CardScript`. Only power is dynamic — toughness stays a printed 4 — so this is the single-stat
 * `dynamicPower(...)` helper (Enigma Drake's shape), not `dynamicStats(...)`.
 */
val DakmorSorceress = card("Dakmor Sorceress") {
    manaCost = "{5}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Wizard Sorcerer"
    oracleText = "Dakmor Sorceress's power is equal to the number of Swamps you control."
    toughness = 4

    dynamicPower(
        DynamicAmounts.battlefield(Player.You, GameObjectFilter.Land.withSubtype("Swamp")).count()
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "71"
        artist = "Matthew D. Wilson"
        imageUri = "https://cards.scryfall.io/normal/front/9/8/985ef9ad-b842-40b7-8d56-98143ecef0dc.jpg"
    }
}
