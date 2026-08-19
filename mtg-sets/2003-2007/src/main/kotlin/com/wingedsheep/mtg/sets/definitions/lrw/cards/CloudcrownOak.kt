package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cloudcrown Oak
 * {2}{G}{G}
 * Creature — Treefolk Warrior
 * 3/4
 * Reach (This creature can block creatures with flying.)
 */
val CloudcrownOak = card("Cloudcrown Oak") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Treefolk Warrior"
    power = 3
    toughness = 4
    oracleText = "Reach (This creature can block creatures with flying.)"

    keywords(Keyword.REACH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "201"
        artist = "Rebecca Guay"
        flavorText = "\"Clever folk build their homes near cloudcrowns. If a hawk or even just a faerie tries to swoop in, it'll get swatted from here to Cloverdell.\"\n—Calydd, kithkin farmer"
        imageUri = "https://cards.scryfall.io/normal/front/e/7/e742366f-e726-406e-bcbe-51a3bfd0151e.jpg?1783942868"
    }
}
