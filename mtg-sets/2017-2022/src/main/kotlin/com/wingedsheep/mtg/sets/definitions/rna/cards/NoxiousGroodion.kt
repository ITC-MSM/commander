package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Noxious Groodion — Ravnica Allegiance #78
 * {2}{B} · Creature — Beast · 2 / 2
 *
 * Vanilla deathtouch.
 */
val NoxiousGroodion = card("Noxious Groodion") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Beast"
    power = 2
    toughness = 2
    oracleText = "Deathtouch"

    keywords(Keyword.DEATHTOUCH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "78"
        artist = "Simon Dominic"
        flavorText = "\"Behold the groodion! Ichor-slurper, oozing fiend. Foulest wonder underground. Grandest vermin of them all!\"\n" +
        "—Zalin the Gutter Bard"
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b6cb3d78-1a60-4e9b-b387-afeb58677536.jpg"
    }
}
