package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hornet Cobra
 * {1}{G}{G}
 * Creature — Snake
 * 2/1
 *
 * First strike
 */
val HornetCobra = card("Hornet Cobra") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Snake"
    power = 2
    toughness = 1
    oracleText = "First strike"

    keywords(Keyword.FIRST_STRIKE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "190"
        artist = "Sandra Everingham"
        flavorText = "\"Then inch by inch out of the grass rose up the head and spread hood of Nag, the big black " +
            "cobra, and he was five feet long from tongue to tail.\"\n" +
            "—Rudyard Kipling, *The Jungle Books*"
        imageUri = "https://cards.scryfall.io/normal/front/2/7/27180bad-9bbc-462b-8832-626dc403a3fd.jpg?1783948047"
    }
}
