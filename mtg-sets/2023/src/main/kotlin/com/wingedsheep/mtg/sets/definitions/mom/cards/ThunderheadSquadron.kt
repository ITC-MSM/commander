package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Thunderhead Squadron
 * {5}{U}
 * Creature — Human Knight
 * 3/4
 * Convoke
 * Flying
 */
val ThunderheadSquadron = card("Thunderhead Squadron") {
    manaCost = "{5}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Knight"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while " +
        "casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Flying"
    power = 3
    toughness = 4

    keywords(Keyword.CONVOKE, Keyword.FLYING)

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "81"
        artist = "PINDURSKI"
        flavorText = "Just as all seemed lost, the griffin riders of Zhalfir arrived with the " +
            "force of a gathered storm."
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8eaa0946-591f-4136-9d8a-e56934151383.jpg?1783917023"
        ruling(
            "2024-01-12",
            "When calculating a spell's total cost, include any alternative costs, additional " +
                "costs, or anything else that increases or reduces the cost to cast the spell. " +
                "Convoke applies after the total cost is calculated. Convoke doesn't change a " +
                "spell's mana cost or mana value."
        )
    }
}
