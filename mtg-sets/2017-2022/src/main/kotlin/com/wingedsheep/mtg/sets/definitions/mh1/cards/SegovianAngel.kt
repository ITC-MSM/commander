package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Segovian Angel — Modern Horizons #25
 * {W} · Creature — Angel · 1 / 1
 *
 * Flying, vigilance
 */
val SegovianAngel = card("Segovian Angel") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Angel"
    power = 1
    toughness = 1
    oracleText = "Flying, vigilance"

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "25"
        artist = "Simon Dominic"
        flavorText = "When Worzel summoned Segovian angels to fight Thomil's Gargantikari gnats, the ensuing battle numbered among the Multiverse's least destructive."
        imageUri = "https://cards.scryfall.io/normal/front/b/5/b5dbaec5-502d-48c2-9e71-c12cd0bccc6a.jpg?1783933157"
    }
}
