package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Azorius Knight-Arbiter — Ravnica Allegiance #154
 * {3}{W}{U} · Creature — Human Knight · 2 / 5
 *
 * "This creature can't be blocked" is unconditional evasion with no filter, which is an
 * [AbilityFlag] rather than a [Keyword] — the block-legality check reads the flag set
 * directly. Vigilance is an ordinary keyword.
 */
val AzoriusKnightArbiter = card("Azorius Knight-Arbiter") {
    manaCost = "{3}{W}{U}"
    colorIdentity = "UW"
    typeLine = "Creature — Human Knight"
    power = 2
    toughness = 5
    oracleText = "Vigilance\n" +
        "This creature can't be blocked."

    keywords(Keyword.VIGILANCE)
    flags(AbilityFlag.CANT_BE_BLOCKED)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "154"
        artist = "Even Amundsen"
        flavorText = "Thanks to the magic in his Writ of Passage, alms beasts lumbered aside, anarchs bowed their heads, and even Rakdos acrobats rolled their spikewheels out of his way."
        imageUri = "https://cards.scryfall.io/normal/front/6/0/60befc28-2ab8-4b59-a33f-0328c5d2f995.jpg"
    }
}
