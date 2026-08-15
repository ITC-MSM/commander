package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Billowing Shriekmass
 * {3}{B}
 * Creature — Spirit
 * 2/3
 *
 * Flying
 * When this creature enters, mill three cards.
 * Threshold — This creature gets +2/+1 as long as there are seven or more cards in your graveyard.
 */
val BillowingShriekmass = card("Billowing Shriekmass") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Spirit"
    power = 2
    toughness = 3
    oracleText = "Flying\nWhen this creature enters, mill three cards. (Put the top three cards of your library into your graveyard.)\nThreshold — This creature gets +2/+1 as long as there are seven or more cards in your graveyard."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.mill(3)
    }

    // Threshold: +2/+1 as long as seven or more cards in your graveyard.
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(2, 1, GroupFilter.source()),
            condition = Conditions.CardsInGraveyardAtLeast(7)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "56"
        artist = "Brent Hollowell"
        imageUri = "https://cards.scryfall.io/normal/front/7/b/7b3587a9-0667-4d53-807b-c437bcb1d7b3.jpg?1782689217"
    }
}
