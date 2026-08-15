package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Naga Eternal
 * {2}{U}
 * Creature — Zombie Snake
 * 3/2
 *
 * Vanilla — no rules text.
 */
val NagaEternal = card("Naga Eternal") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Zombie Snake"
    power = 3
    toughness = 2

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "60"
        artist = "Johann Bodin"
        flavorText = "\"I recognize that headdress. This one was feared even by her fellow initiates.\"\n—Samut"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f244233-f2e8-48f8-9106-e7cd186efd51.jpg?1783933461"
    }
}
