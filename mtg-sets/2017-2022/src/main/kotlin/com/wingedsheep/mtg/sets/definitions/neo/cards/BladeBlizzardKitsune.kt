package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.ninjutsu
import com.wingedsheep.sdk.model.Rarity

/**
 * Blade-Blizzard Kitsune — Kamigawa: Neon Dynasty #5 (canonical printing)
 * {2}{W} · Creature — Fox Ninja · 2/2
 *
 * Ninjutsu {3}{W}
 * Double strike
 *
 * Ninjutsu costs more than the card's own mana cost here, which is the point: sneaking it in
 * mid-combat means it connects with double strike the same turn.
 */
val BladeBlizzardKitsune = card("Blade-Blizzard Kitsune") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Fox Ninja"
    power = 2
    toughness = 2
    oracleText = "Ninjutsu {3}{W} ({3}{W}, Return an unblocked attacker you control to hand: Put " +
        "this card onto the battlefield from your hand tapped and attacking.)\nDouble strike"

    ninjutsu("{3}{W}")
    keywords(Keyword.DOUBLE_STRIKE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "5"
        artist = "Andrew Mar"
        flavorText = "In the time it takes most to master one blade, he mastered two."
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d8419d27-8c6e-4f38-98b4-60dd9a910c43.jpg?1783923926"
    }
}
