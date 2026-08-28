package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Investigator's Journal
 * {2}
 * Artifact — Book Clue
 *
 * This artifact enters with a number of suspect counters on it equal to the greatest number of
 * creatures a player controls.
 * {2}, {T}, Remove a suspect counter from this artifact: Draw a card.
 * {2}, Sacrifice this artifact: Draw a card.
 *
 * The enters-with amount is the superlative, not the total: no opponent is chosen and the largest
 * single player's creature count is always used (its only ruling), which is
 * [DynamicAmounts.greatestControlledBySinglePlayer] rather than a `Player.Each` count — the latter
 * would sum the table. The Journal's own controller is one of the players measured, so a board it
 * is winning on still fills it.
 *
 * The suspect counter is a passive store with no inherent rule; it shares no machinery with the
 * *suspected* keyword action (CR 701.58), which places no counter at all.
 */
val InvestigatorsJournal = card("Investigator's Journal") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact — Book Clue"
    oracleText = "This artifact enters with a number of suspect counters on it equal to the " +
        "greatest number of creatures a player controls.\n" +
        "{2}, {T}, Remove a suspect counter from this artifact: Draw a card.\n" +
        "{2}, Sacrifice this artifact: Draw a card."

    replacementEffect(
        EntersWithDynamicCounters(
            counterType = CounterTypeFilter.Named(Counters.SUSPECT),
            count = DynamicAmounts.greatestControlledBySinglePlayer(GameObjectFilter.Creature),
        )
    )

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.Tap,
            Costs.RemoveCounterFromSelf(Counters.SUSPECT),
        )
        effect = Effects.DrawCards(1)
        description = "{2}, {T}, Remove a suspect counter from this artifact: Draw a card."
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.SacrificeSelf,
        )
        effect = Effects.DrawCards(1)
        description = "{2}, Sacrifice this artifact: Draw a card."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "258"
        artist = "Yeong-Hao Han"
        imageUri = "https://cards.scryfall.io/normal/front/f/8/f8a606e5-7a90-4a89-b51a-7f4e68705f97.jpg?1783924786"
        ruling(
            "2021-11-19",
            "You don't choose an opponent to determine how many suspect counters are placed on " +
                "Investigator's Journal as it enters the battlefield. You always use the greatest " +
                "number possible.",
        )
    }
}
