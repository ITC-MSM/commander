package com.wingedsheep.mtg.sets.definitions.grn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pitiless Gorgon
 * {1}{B/G}{B/G}
 * Creature — Gorgon
 * 2/2
 * Deathtouch
 */
val PitilessGorgon = card("Pitiless Gorgon") {
    manaCost = "{1}{B/G}{B/G}"
    colorIdentity = "BG"
    typeLine = "Creature — Gorgon"
    oracleText = "Deathtouch"
    power = 2
    toughness = 2

    keywords(Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "218"
        artist = "Alex Konstad"
        flavorText = "\"The reign of the Swarm begins. Let us rise now and dress ourselves in vengeance.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c1a925a0-9d26-441b-8b4a-0614a0485fe6.jpg?1783934114"
    }
}
