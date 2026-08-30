package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Updraft Elemental
 * {2}{U}
 * Creature — Elemental
 * 1 / 4
 *
 * Flying
 *
 * Evasion only — one keyword, no script.
 */
val UpdraftElemental = card("Updraft Elemental") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental"
    power = 1
    toughness = 4
    oracleText = "Flying"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "82"
        artist = "Raf Sarmento"
        flavorText = "\"It slips through the smallest cracks in the mountain, emerging whole and unfettered. There is nowhere it cannot go, for what can hold back the air itself?\"\n—Chanyi, Ojutai monk"
        imageUri = "https://cards.scryfall.io/normal/front/9/6/9621c700-569d-4d07-847e-68b97113415f.jpg?1783938601"
    }
}
