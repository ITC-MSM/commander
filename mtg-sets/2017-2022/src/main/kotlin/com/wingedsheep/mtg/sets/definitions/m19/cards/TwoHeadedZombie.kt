package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Two-Headed Zombie
 * {3}{B}
 * Creature — Zombie
 * 4/2
 * Menace (This creature can't be blocked except by two or more creatures.)
 */
val TwoHeadedZombie = card("Two-Headed Zombie") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 4
    toughness = 2
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)"

    keywords(Keyword.MENACE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "123"
        artist = "Josh Hass"
        flavorText = "\"Thread the torsos together with angel hair to ensure they will cooperate in battle.\"\n" +
            "—*The Stitcher's Tome*"
        imageUri = "https://cards.scryfall.io/normal/front/2/c/2cc5760e-8b27-4d37-9772-c9eda90b1d95.jpg"
    }
}
