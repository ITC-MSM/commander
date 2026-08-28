package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedExceptBy
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Prowling Nightstalker
 * {3}{B}
 * Creature — Nightstalker
 * 2/2
 *
 * This creature can't be blocked except by black creatures.
 */
val ProwlingNightstalker = card("Prowling Nightstalker") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Nightstalker"
    oracleText = "This creature can't be blocked except by black creatures."
    power = 2
    toughness = 2

    staticAbility {
        ability = CantBeBlockedExceptBy(blockerFilter = GameObjectFilter.Creature.withColor(Color.BLACK))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "83"
        artist = "Keith Parkinson"
        flavorText = "Silent as a serpent, twisted as a lone bog tree, evil as Tojira's heart."
        imageUri = "https://cards.scryfall.io/normal/front/7/4/749e3992-693b-4b4f-b410-b5f2faac0040.jpg"
    }
}
