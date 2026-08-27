package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Guardians of Meletis
 * {3}
 * Artifact Creature — Golem
 * 0 / 6
 *
 * Defender (This creature can't attack.)
 */
val GuardiansOfMeletis = card("Guardians of Meletis") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Golem"
    power = 0
    toughness = 6
    oracleText = "Defender (This creature can't attack.)"

    keywords(Keyword.DEFENDER)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "217"
        artist = "Magali Villeneuve"
        flavorText = "The histories speak of two feuding rulers whose deaths were celebrated and whose monuments symbolized the end of their wars. In truth they were peaceful lovers, their story lost to the ages."
        imageUri = "https://cards.scryfall.io/normal/front/8/5/85284586-7a9d-4344-aebd-f0e072c1f266.jpg"
    }
}
