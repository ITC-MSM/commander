package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantDynamicStatsEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Dark Matter Manipulator — "+2/+0 for every seven cards" is twice the number of complete sets of
 * seven, so the bonus is `2 × floor(graveyard / 7)`: 0–6 cards give +0, 7–13 give +2, 14–20 give +4.
 */
val DarkMatterManipulator = card("Dark Matter Manipulator") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Warlock"
    power = 1
    toughness = 2
    oracleText = "When this creature enters, mill three cards.\n" +
        "This creature gets +2/+0 for every seven cards in your graveyard."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.mill(3)
        description = "When this creature enters, mill three cards."
    }

    staticAbility {
        ability = GrantDynamicStatsEffect(
            filter = GroupFilter.source(),
            powerBonus = DynamicAmount.Multiply(
                DynamicAmount.Divide(
                    DynamicAmounts.cardsInYourGraveyard(),
                    DynamicAmount.Fixed(7),
                    roundUp = false,
                ),
                2,
            ),
            toughnessBonus = DynamicAmount.Fixed(0),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "52"
        artist = "Steven Belledin"
        flavorText = "Theorix has the most instances, by far, of posthumously awarded extra credit."
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4ec912d5-cbe7-4d07-9ece-b03ac02d3055.jpg?1789385959"
        inBooster = false
    }
}
