package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Inkwell Leviathan
 * {7}{U}{U}
 * Artifact Creature — Leviathan
 * 7 / 11
 * Trample
 * Islandwalk
 * Shroud
 *
 * Three printed keywords and nothing else — every one of them is a live [Keyword] the engine
 * reads (trample in combat damage assignment, islandwalk as an evasion check against the
 * defending player's lands, shroud in target legality), so the card is exactly its keyword list.
 */
val InkwellLeviathan = card("Inkwell Leviathan") {
    manaCost = "{7}{U}{U}"
    colorIdentity = "U"
    typeLine = "Artifact Creature — Leviathan"
    power = 7
    toughness = 11
    oracleText = "Trample\n" +
        "Islandwalk (This creature can't be blocked as long as defending player controls an Island.)\n" +
        "Shroud (This creature can't be the target of spells or abilities.)"

    keywords(Keyword.TRAMPLE, Keyword.ISLANDWALK, Keyword.SHROUD)

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "30"
        artist = "Anthony Francisco"
        flavorText = "\"Into its maw went the seventh sea, never to be seen again while the world remains.\" —Esper fable"
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cb380a5a-a3f9-42fb-ac5c-f54afa3c1079.jpg"
    }
}
