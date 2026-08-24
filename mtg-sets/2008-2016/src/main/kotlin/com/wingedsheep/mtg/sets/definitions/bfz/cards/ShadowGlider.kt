package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shadow Glider
 * {2}{W}
 * Creature — Kor Soldier
 * 2/2
 * Flying
 */
val ShadowGlider = card("Shadow Glider") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kor Soldier"
    power = 2
    toughness = 2
    oracleText = "Flying"

    keywords(Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "47"
        artist = "Steve Prescott"
        flavorText = "A few bands of kor sought refuge from the Eldrazi in Zendikar's vast cave networks, relying " +
            "on their ability to survive in harsh vertical landscapes."
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b4ffaf62-2e12-4b1d-a590-f63aacb4a30b.jpg?1783938215"
    }
}
