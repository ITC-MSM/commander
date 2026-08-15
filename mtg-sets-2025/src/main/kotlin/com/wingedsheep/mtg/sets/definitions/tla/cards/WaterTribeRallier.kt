package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Water Tribe Rallier
 * {1}{W}
 * Creature — Human Soldier Ally
 * 2/2
 *
 * Waterbend {5}: Look at the top four cards of your library. You may reveal a creature card
 * with power 3 or less from among them and put it into your hand. Put the rest on the bottom
 * of your library in a random order. (While paying a waterbend cost, you can tap your artifacts
 * and creatures to help. Each one pays for {1}.)
 *
 * Implementation notes:
 *  - "Waterbend {5}" is an activated ability whose mana cost carries the waterbend
 *    alternative-cost flag ([com.wingedsheep.sdk.scripting.ActivatedAbility] `hasWaterbend`);
 *    the reminder text (tap artifacts/creatures to pay {1} each) is supplied by the flag.
 *  - The effect is the Star Charter "look at top four, reveal a small creature to hand, rest to
 *    the bottom in a random order" shape via [Patterns.Library.lookAtTopRevealMatchingToHand].
 */
val WaterTribeRallier = card("Water Tribe Rallier") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier Ally"
    oracleText = "Waterbend {5}: Look at the top four cards of your library. You may reveal a " +
        "creature card with power 3 or less from among them and put it into your hand. Put the " +
        "rest on the bottom of your library in a random order. (While paying a waterbend cost, " +
        "you can tap your artifacts and creatures to help. Each one pays for {1}.)"
    power = 2
    toughness = 2

    // Waterbend {5}: Look at the top four cards of your library...
    activatedAbility {
        cost = Costs.Mana("{5}")
        hasWaterbend = true
        effect = Patterns.Library.lookAtTopRevealMatchingToHand(
            count = DynamicAmount.Fixed(4),
            filter = GameObjectFilter.Creature.powerAtMost(3),
            prompt = "You may reveal a creature card with power 3 or less and put it into your hand"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "42"
        artist = "Boell Oyino"
        imageUri = "https://cards.scryfall.io/normal/front/4/e/4e744b6c-1c2f-451a-818a-5ee7785b5213.jpg?1764120175"
    }
}
