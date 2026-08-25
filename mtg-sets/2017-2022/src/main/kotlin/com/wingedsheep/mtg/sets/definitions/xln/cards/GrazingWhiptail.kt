package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Grazing Whiptail
 * {2}{G}{G}
 * Creature — Dinosaur
 * 3/4
 *
 * Reach
 */
val GrazingWhiptail = card("Grazing Whiptail") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dinosaur"
    oracleText = "Reach"
    power = 3
    toughness = 4

    keywords(Keyword.REACH)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "190"
        artist = "Gabor Szikszai"
        flavorText = "Often found browsing on the upper canopies of Ixalan's jungles, whiptails are known to absently bat away anything foolish enough to interrupt their meal."
        imageUri = "https://cards.scryfall.io/normal/front/7/5/75cbd690-4d6e-49a7-bded-e6ecee2c76b6.jpg"
    }
}
