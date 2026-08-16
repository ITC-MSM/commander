package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Shivan Branch-Burner
 * {5}{R}{R}
 * Creature — Dragon
 * 4/4
 * Convoke
 * Flying, haste
 */
val ShivanBranchBurner = card("Shivan Branch-Burner") {
    manaCost = "{5}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dragon"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while " +
        "casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Flying, haste"
    power = 4
    toughness = 4

    keywords(Keyword.CONVOKE, Keyword.FLYING, Keyword.HASTE)

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "165"
        artist = "Aaron Miller"
        flavorText = "The New Coalition gave Shiv time to prepare a warm welcome for the invaders."
        imageUri = "https://cards.scryfall.io/normal/front/e/5/e585631c-bd41-4b48-aac4-4b6f5636ecd5.jpg?1783916980"
        ruling(
            "2024-01-12",
            "When calculating a spell's total cost, include any alternative costs, additional " +
                "costs, or anything else that increases or reduces the cost to cast the spell. " +
                "Convoke applies after the total cost is calculated. Convoke doesn't change a " +
                "spell's mana cost or mana value."
        )
    }
}
