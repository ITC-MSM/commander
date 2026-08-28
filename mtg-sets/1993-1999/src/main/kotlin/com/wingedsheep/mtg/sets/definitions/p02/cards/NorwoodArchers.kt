package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Norwood Archers
 * {3}{G}
 * Creature — Elf Archer
 * 3/3
 * Reach (This creature can block creatures with flying.)
 */
val NorwoodArchers = card("Norwood Archers") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Archer"
    oracleText = "Reach (This creature can block creatures with flying.)"
    power = 3
    toughness = 3
    keywords(Keyword.REACH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "136"
        artist = "Rebecca Guay"
        flavorText = "\"'Air superiority?' Not while our archers scan the skies.\"\n—Elvish scout"
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c545fa63-d0dd-422b-8d3b-88b444f13fce.jpg"
    }
}
