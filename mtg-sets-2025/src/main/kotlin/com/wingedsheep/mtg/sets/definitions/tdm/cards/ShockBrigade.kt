package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.mobilize
import com.wingedsheep.sdk.model.Rarity

/**
 * Shock Brigade — Tarkir: Dragonstorm #120
 * {1}{R} · Creature — Goblin Soldier · 1/3
 *
 * Menace
 * Mobilize 1 (Whenever this creature attacks, create a tapped and attacking 1/1 red Warrior
 * creature token. Sacrifice it at the beginning of the next end step.)
 *
 * Both abilities are keyword helpers: `keywords(Keyword.MENACE)` for the evasion keyword and
 * the `mobilize(n)` builder helper, which adds the display-only "Mobilize 1" keyword ability
 * plus the attack-triggered ability that creates the tapped-and-attacking Warrior token and
 * schedules its sacrifice at the next end step.
 */
val ShockBrigade = card("Shock Brigade") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Soldier"
    power = 1
    toughness = 3
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "Mobilize 1 (Whenever this creature attacks, create a tapped and attacking 1/1 red Warrior creature token. Sacrifice it at the beginning of the next end step.)"

    keywords(Keyword.MENACE)
    mobilize(1)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "120"
        artist = "Fajareka Setiawan"
        flavorText = "\"From Alesha to Zurgo, all Mardu khans started right here. At the front lines.\""
        imageUri = "https://cards.scryfall.io/normal/front/6/6/66940466-8e9d-4a85-bfb0-e92189b7a121.jpg?1743204444"
    }
}
