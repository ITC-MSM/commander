package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.ninjutsu
import com.wingedsheep.sdk.model.Rarity

/**
 * Mukotai Ambusher — Kamigawa: Neon Dynasty #112 (canonical printing)
 * {3}{B} · Artifact Creature — Rat Ninja · 3/2
 *
 * Ninjutsu {1}{B}
 * Lifelink
 */
val MukotaiAmbusher = card("Mukotai Ambusher") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Artifact Creature — Rat Ninja"
    power = 3
    toughness = 2
    oracleText = "Ninjutsu {1}{B} ({1}{B}, Return an unblocked attacker you control to hand: Put " +
        "this card onto the battlefield from your hand tapped and attacking.)\nLifelink"

    ninjutsu("{1}{B}")
    keywords(Keyword.LIFELINK)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "112"
        artist = "Raymond Swanland"
        flavorText = "The Mukotai Reckoners specialize in stealth and subterfuge."
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0adf3293-c1e0-447c-8231-26fa9476a262.jpg?1783923879"
    }
}
