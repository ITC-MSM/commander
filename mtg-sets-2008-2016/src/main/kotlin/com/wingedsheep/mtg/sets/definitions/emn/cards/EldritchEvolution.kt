package com.wingedsheep.mtg.sets.definitions.emn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Eldritch Evolution
 * {1}{G}{G}
 * Sorcery
 * As an additional cost to cast this spell, sacrifice a creature.
 * Search your library for a creature card with mana value X or less, where X is 2 plus the
 * sacrificed creature's mana value. Put that card onto the battlefield, then shuffle. Exile
 * Eldritch Evolution.
 *
 * The sacrificed creature's mana value is read from the cost-payment snapshot (last-known
 * information via [EntityReference.Sacrificed], as in Metamorphosis). The search cap is the
 * general [DynamicAmount]-backed mana-value predicate (`manaValueAtMostDynamic`) applied to a
 * creature filter, fed `2 + the sacrificed creature's mana value`. The card exiles itself on
 * resolution via [selfExile].
 */
val EldritchEvolution = card("Eldritch Evolution") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, sacrifice a creature.\n" +
        "Search your library for a creature card with mana value X or less, where X is 2 plus " +
        "the sacrificed creature's mana value. Put that card onto the battlefield, then shuffle. " +
        "Exile Eldritch Evolution."

    additionalCost(Costs.additional.SacrificePermanent(GameObjectFilter.Creature))

    spell {
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Creature.manaValueAtMostDynamic(
                DynamicAmount.Add(
                    DynamicAmount.Fixed(2),
                    DynamicAmount.EntityProperty(
                        EntityReference.Sacrificed(0),
                        EntityNumericProperty.ManaValue
                    )
                )
            ),
            count = 1,
            destination = SearchDestination.BATTLEFIELD,
            shuffleAfter = true
        )
        selfExile()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "155"
        artist = "Jason Rainville"
        imageUri = "https://cards.scryfall.io/normal/front/e/f/efcb00e5-2caa-45c8-ad19-05d45c683d16.jpg?1782711841"
    }
}
