package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.ninjutsu
import com.wingedsheep.sdk.model.Rarity

/**
 * Dokuchi Shadow-Walker — Kamigawa: Neon Dynasty #94 (canonical printing)
 * {4}{B}{B} · Creature — Ogre Ninja · 5/5
 *
 * Ninjutsu {3}{B}
 *
 * The whole card is the discount: a 5/5 body for two mana less than its printed cost, provided
 * an unblocked attacker goes back to hand for it.
 */
val DokuchiShadowWalker = card("Dokuchi Shadow-Walker") {
    manaCost = "{4}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Ogre Ninja"
    power = 5
    toughness = 5
    oracleText = "Ninjutsu {3}{B} ({3}{B}, Return an unblocked attacker you control to hand: Put " +
        "this card onto the battlefield from your hand tapped and attacking.)"

    ninjutsu("{3}{B}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "94"
        artist = "Manuel Castañón"
        flavorText = "Don't fear the blade, fear the shadows."
        imageUri = "https://cards.scryfall.io/normal/front/8/1/81496a81-c986-4749-a28c-45341764e28f.jpg?1783923888"
    }
}
