package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Merciless Javelineer
 * {2}{B}{R}
 * Creature — Minotaur Warrior
 * 4/2
 * {2}, Discard a card: Put a -1/-1 counter on target creature. That creature can't block this turn.
 *
 * "That creature" is the same bound target as the counter, so both halves of the composite point
 * at the one target requirement rather than declaring a second one.
 */
val MercilessJavelineer = card("Merciless Javelineer") {
    manaCost = "{2}{B}{R}"
    colorIdentity = "BR"
    typeLine = "Creature — Minotaur Warrior"
    oracleText = "{2}, Discard a card: Put a -1/-1 counter on target creature. That creature can't block this turn."
    power = 4
    toughness = 2

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.DiscardCard)
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.AddCounters(Counters.MINUS_ONE_MINUS_ONE, 1, t),
            Effects.CantBlock(t),
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "202"
        artist = "Nils Hamm"
        flavorText = "\"My mind is the calm in the midst of the storm, and my javelin the lightning.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/e/ee0e8c57-3046-414d-be00-39bfb2537026.jpg?1783936462"
    }
}
