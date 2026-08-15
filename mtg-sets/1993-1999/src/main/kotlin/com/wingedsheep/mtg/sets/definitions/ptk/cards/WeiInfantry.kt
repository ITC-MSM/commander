package com.wingedsheep.mtg.sets.definitions.ptk.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wei Infantry
 * {1}{B}
 * Creature — Human Soldier
 * 2/1
 *
 * Vanilla — no rules text.
 */
val WeiInfantry = card("Wei Infantry") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 1

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "88"
        artist = "LHQ"
        flavorText = "Wei won the battle of Hefei when Zhang Liao defeated Sun Quan and then foiled a Wu scheme to incite rebellion."
        imageUri = "https://cards.scryfall.io/normal/front/7/2/72c6465f-3144-4faf-b248-a9fb941dc002.jpg?1783946112"
    }
}
