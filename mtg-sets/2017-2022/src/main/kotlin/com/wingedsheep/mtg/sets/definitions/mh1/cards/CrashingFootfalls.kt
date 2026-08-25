package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Crashing Footfalls — Modern Horizons #160
 * (no mana cost) · Sorcery
 *
 * Suspend 4—{G}
 * Create two 4/4 green Rhino creature tokens with trample.
 *
 * Printed with **no mana cost** — CR 202.1b/118.6 make that an unpayable cost, so suspend is the
 * only way to put this on the stack (barring another free-cast effect). `manaCost = ""` is what
 * `CardBuilder.build()` reads to set `hasNoManaCost`; `"{0}"` would parse to a payable zero cost
 * and leave the card castable for free, which is a different card. Same shape as
 * [com.wingedsheep.mtg.sets.definitions.mh2.cards.SolTalisman].
 *
 * Green is a printed **color indicator** (CR 204), not derived from a mana cost that doesn't
 * exist, so `colorIndicator` carries it alongside `colorIdentity` — exactly as Ancestral Vision
 * does for blue. Without it the card would be colorless everywhere but the deckbuilder.
 *
 * The display-only `Keyword.SUSPEND` is derived from the parameterized [KeywordAbility.Suspend]
 * by `CardBuilder.build()`, so only the ability is declared here.
 */
val CrashingFootfalls = card("Crashing Footfalls") {
    manaCost = ""
    colorIdentity = "G"
    colorIndicator = "G"
    typeLine = "Sorcery"
    oracleText = "Suspend 4—{G} (Rather than cast this card from your hand, pay {G} and exile it with four time counters on it. At the beginning of your upkeep, remove a time counter. When the last is removed, you may cast it without paying its mana cost.)\n" +
        "Create two 4/4 green Rhino creature tokens with trample."

    keywordAbility(KeywordAbility.suspend("{G}", 4))

    spell {
        effect = Effects.CreateToken(
            power = 4,
            toughness = 4,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Rhino"),
            keywords = setOf(Keyword.TRAMPLE),
            count = 2
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "160"
        artist = "Dan Murayama Scott"
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a8cca2a2-69e3-4136-936c-7a2774c19351.jpg?1783933099"
    }
}
