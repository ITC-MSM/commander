package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Dreadwing Scavenger
 * {1}{U}{B}
 * Creature — Nightmare Bird
 * 2/2
 *
 * Flying
 * Whenever this creature enters or attacks, draw a card, then discard a card.
 * Threshold — This creature gets +1/+1 and has deathtouch as long as there are seven or more
 * cards in your graveyard.
 *
 * "Enters or attacks" is modeled as the established pair of triggered abilities (one on
 * [Triggers.EntersBattlefield], one on [Triggers.Attacks]), each running the [Patterns.Hand.loot]
 * draw-then-discard. The threshold buff is two [ConditionalStaticAbility]s gated on
 * [Conditions.CardsInGraveyardAtLeast] — one stat buff, one keyword grant — so it turns on and
 * off continuously with the graveyard count.
 */
val DreadwingScavenger = card("Dreadwing Scavenger") {
    manaCost = "{1}{U}{B}"
    colorIdentity = "UB"
    typeLine = "Creature — Nightmare Bird"
    power = 2
    toughness = 2
    oracleText = "Flying\n" +
        "Whenever this creature enters or attacks, draw a card, then discard a card.\n" +
        "Threshold — This creature gets +1/+1 and has deathtouch as long as there are seven or " +
        "more cards in your graveyard."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Hand.loot(draw = 1, discard = 1)
    }

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Patterns.Hand.loot(draw = 1, discard = 1)
    }

    // Threshold: +1/+1
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = ModifyStats(1, 1, Filters.Self),
            condition = Conditions.CardsInGraveyardAtLeast(7)
        )
    }

    // Threshold: deathtouch
    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.DEATHTOUCH, Filters.Self),
            condition = Conditions.CardsInGraveyardAtLeast(7)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "118"
        artist = "Xavier Ribeiro"
        flavorText = "It scours battlefields for corpses, consuming flesh and souls alike."
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e24d838b-ab48-410a-9a50-dbfea5da089b.jpg?1782689165"
    }
}
