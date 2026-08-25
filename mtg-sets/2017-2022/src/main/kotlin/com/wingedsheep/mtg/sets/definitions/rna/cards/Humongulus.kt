package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Humongulus — Ravnica Allegiance #41
 * {4}{U} · Creature — Homunculus · 2 / 5
 *
 * Hexproof and nothing else. The reminder text is printed, so it lives in `oracleText`;
 * the keyword itself is the whole rules content.
 */
val Humongulus = card("Humongulus") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Homunculus"
    power = 2
    toughness = 5
    oracleText = "Hexproof (This creature can't be the target of spells or abilities your opponents control.)"

    keywords(Keyword.HEXPROOF)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "41"
        artist = "Jesper Ejsing"
        flavorText = "Searching the city for Fblthp felt like sifting the rain for a single drop of blood."
        imageUri = "https://cards.scryfall.io/normal/front/2/1/21982dc7-4f79-4251-8382-95cd1f627e0f.jpg"
    }
}
