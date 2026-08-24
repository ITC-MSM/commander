package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hired Blade
 * {2}{B}
 * Creature — Human Assassin
 * 3/2
 * Flash (You may cast this spell any time you could cast an instant.)
 */
val HiredBlade = card("Hired Blade") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Assassin"
    power = 3
    toughness = 2
    oracleText = "Flash (You may cast this spell any time you could cast an instant.)"

    keywords(Keyword.FLASH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "100"
        artist = "Mark Behm"
        flavorText = "\"If you want them dead, buy some poison. If you want them to have the worst day of their life before dying, then let's talk price.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/6/7624c9aa-2f47-4fcc-a9fe-cc843e8de053.jpg"
    }
}
