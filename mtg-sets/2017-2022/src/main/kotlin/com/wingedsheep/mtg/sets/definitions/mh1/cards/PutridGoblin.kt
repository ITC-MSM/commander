package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Putrid Goblin
 * {1}{B}
 * Creature — Zombie Goblin
 * 2/2
 * Persist (When this creature dies, if it had no -1/-1 counters on it, return it to the battlefield under its owner's control with a -1/-1 counter on it.)
 *
 * Persist is engine-live: [Keyword.PERSIST] is read by the death-trigger detector, so the keyword
 * alone carries the whole behaviour. No triggered ability is authored for the reminder text.
 */
val PutridGoblin = card("Putrid Goblin") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie Goblin"
    power = 2
    toughness = 2
    oracleText = "Persist (When this creature dies, if it had no -1/-1 counters on it, return it to the battlefield under its owner's control with a -1/-1 counter on it.)"

    keywords(Keyword.PERSIST)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "101"
        artist = "Winona Nelson"
        flavorText = "Too stupid to survive, too dumb to die."
        imageUri = "https://cards.scryfall.io/normal/front/3/3/333406d5-abcc-4629-a33b-395d0662ba1b.jpg?1783933123"
    }
}
