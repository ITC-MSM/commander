package com.wingedsheep.mtg.sets.definitions.isd.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Stitched Drake
 * {1}{U}{U}
 * Creature — Zombie Drake
 * 3/4
 * As an additional cost to cast this spell, exile a creature card from your graveyard.
 * Flying
 */
val StitchedDrake = card("Stitched Drake") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Zombie Drake"
    oracleText =
        "As an additional cost to cast this spell, exile a creature card from your graveyard.\nFlying"
    power = 3
    toughness = 4

    keywords(Keyword.FLYING)

    additionalCost(Costs.additional.ExileCards(count = 1, filter = GameObjectFilter.Creature))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "80"
        artist = "Chris Rahn"
        flavorText =
            "\"The best skaab are more powerful and more beautiful than the sum of their parts.\"\n—Stitcher Geralf"
        imageUri =
            "https://cards.scryfall.io/normal/front/a/d/ad81266a-488f-449a-9daf-637727564865.jpg?1782714763"
    }
}
