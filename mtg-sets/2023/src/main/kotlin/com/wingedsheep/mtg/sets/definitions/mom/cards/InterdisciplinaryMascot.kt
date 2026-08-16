package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.WardCost
import com.wingedsheep.sdk.scripting.effects.ZonePlacement

/**
 * Interdisciplinary Mascot
 * {6}{U}{U}
 * Creature — Elemental Fractal
 * 5/5
 * Convoke
 * Ward {3}
 * When this creature enters, look at the top four cards of your library. Put one of them into your
 * hand and the rest on the bottom of your library in a random order.
 *
 * The entry ability is `Patterns.Library.lookAtTopAndKeep` with the bottom-of-library remainder in
 * [CardOrder.Random] — "in a random order" is the remainder's ordering, not a shuffle.
 */
val InterdisciplinaryMascot = card("Interdisciplinary Mascot") {
    manaCost = "{6}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental Fractal"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while " +
        "casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Ward {3}\n" +
        "When this creature enters, look at the top four cards of your library. Put one of them " +
        "into your hand and the rest on the bottom of your library in a random order."
    power = 5
    toughness = 5

    keywords(Keyword.CONVOKE)
    keywordAbility(KeywordAbility.Ward(WardCost.Mana("{3}")))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.lookAtTopAndKeep(
            count = 4,
            keepCount = 1,
            keepDestination = CardDestination.ToZone(Zone.HAND),
            restDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
            restOrder = CardOrder.Random
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "326"
        artist = "Mathias Kollros"
        imageUri = "https://cards.scryfall.io/normal/front/0/e/0eecdd9a-6c3f-43ee-8d9c-e3466bd7bf5e.jpg?1783916905"
        ruling(
            "2024-01-12",
            "When calculating a spell's total cost, include any alternative costs, additional " +
                "costs, or anything else that increases or reduces the cost to cast the spell. " +
                "Convoke applies after the total cost is calculated. Convoke doesn't change a " +
                "spell's mana cost or mana value."
        )
    }
}
