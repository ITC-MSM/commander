package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Abhorrent Oculus
 * {2}{U}
 * Creature — Eye
 * 5/5
 * As an additional cost to cast this spell, exile six cards from your graveyard.
 * Flying
 * At the beginning of each opponent's upkeep, manifest dread. (Look at the top two cards of your
 * library. Put one onto the battlefield face down as a 2/2 creature and the other into your
 * graveyard. Turn it face up any time for its mana cost if it's a creature card.)
 *
 * The graveyard exile is an [AdditionalCost.ExileFrom] over six cards from your graveyard
 * (CR 601.2f — paid as the spell is cast), so the spell can't be cast without six cards to exile.
 * The upkeep payoff reuses [Patterns.Library.manifestDread] (CR 701.62).
 */
val AbhorrentOculus = card("Abhorrent Oculus") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Eye"
    oracleText = "As an additional cost to cast this spell, exile six cards from your graveyard.\n" +
        "Flying\n" +
        "At the beginning of each opponent's upkeep, manifest dread. (Look at the top two cards of " +
        "your library. Put one onto the battlefield face down as a 2/2 creature and the other into " +
        "your graveyard. Turn it face up any time for its mana cost if it's a creature card.)"
    power = 5
    toughness = 5

    keywords(Keyword.FLYING)

    additionalCost(
        Costs.additional.ExileCards(count = 6)
    )

    triggeredAbility {
        trigger = Triggers.EachOpponentUpkeep
        effect = Patterns.Library.manifestDread()
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "42"
        artist = "Bryan Sola"
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d2705b43-a94a-44c0-8740-82e0b296820c.jpg?1726286015"
    }
}
