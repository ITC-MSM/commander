package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.madness
import com.wingedsheep.sdk.model.Rarity

/**
 * Reckless Wurm
 * {3}{R}{R}
 * Creature — Wurm
 * 4/4
 * Trample
 * Madness {2}{R}
 */
val RecklessWurm = card("Reckless Wurm") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Wurm"
    power = 4
    toughness = 4
    oracleText = "Trample\n" +
        "Madness {2}{R} (If you discard this card, discard it into exile. When you do, cast it for its madness cost or put it into your graveyard.)"

    keywords(Keyword.TRAMPLE)
    madness("{2}{R}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "120"
        artist = "Greg Staples"
        flavorText = "Bred for battle in the Grand Coliseum, these wurms annihilated whole ecosystems when released into the wild."
        imageUri = "https://cards.scryfall.io/normal/front/1/7/17d4fd92-33d1-4f14-a4f4-7c7feaa18f93.jpg"
    }
}
