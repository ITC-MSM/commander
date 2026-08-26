package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cursed Minotaur
 * {2}{B}
 * Creature — Zombie Minotaur
 * 3/2
 * Menace (This creature can't be blocked except by two or more creatures.)
 */
val CursedMinotaur = card("Cursed Minotaur") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Minotaur"
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)"
    power = 3
    toughness = 2

    keywords(Keyword.MENACE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "85"
        artist = "David Palumbo"
        flavorText = "\"Look! That is why we must never waver. That is what awaits us if we fail.\"\n—Djeru, initiate of Tah crop"
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3990d2f-39d9-49f9-936f-1d40adcf295c.jpg?1783936509"
    }
}
