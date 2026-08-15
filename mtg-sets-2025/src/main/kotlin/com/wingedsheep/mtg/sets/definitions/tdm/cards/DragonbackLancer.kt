package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.mobilize
import com.wingedsheep.sdk.model.Rarity

/**
 * Dragonback Lancer — Tarkir: Dragonstorm #9
 * {3}{W} · Creature — Human Soldier · 3/3
 *
 * Flying
 * Mobilize 1 (Whenever this creature attacks, create a tapped and attacking 1/1 red Warrior
 * creature token. Sacrifice it at the beginning of the next end step.)
 *
 * Both abilities are keyword helpers: `keywords(Keyword.FLYING)` for the evasion keyword and the
 * `mobilize(n)` builder helper, which adds the display-only "Mobilize 1" keyword ability plus the
 * attack-triggered ability that creates the tapped-and-attacking Warrior token and schedules its
 * sacrifice at the next end step.
 */
val DragonbackLancer = card("Dragonback Lancer") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "Mobilize 1 (Whenever this creature attacks, create a tapped and attacking 1/1 red Warrior creature token. Sacrifice it at the beginning of the next end step.)"

    keywords(Keyword.FLYING)
    mobilize(1)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "9"
        artist = "Diego Gisbert"
        imageUri = "https://cards.scryfall.io/normal/front/0/2/0200a8c5-3293-48d0-a523-ba148680f588.jpg?1743203987"
    }
}
