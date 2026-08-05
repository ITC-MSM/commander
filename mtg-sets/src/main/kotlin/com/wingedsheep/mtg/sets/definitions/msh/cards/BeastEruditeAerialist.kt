package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GrantKeyword

/**
 * Beast, Erudite Aerialist — Marvel Super Heroes #206
 * {3}{G/U} · Legendary Creature — Mutant Scientist Hero · 3/3
 *
 * As long as you've put one or more +1/+1 counters on Beast this turn, he has flying.
 * Whenever Beast deals combat damage to a player, draw a card.
 *
 * Modeling notes:
 *  - The flying grant is a [ConditionalStaticAbility] wrapping [GrantKeyword] scoped to the
 *    source ([Filters.Self]), gated on [Conditions.SourceReceivedCounterThisTurn]. The engine
 *    evaluates the condition during state projection, so flying appears and disappears in
 *    Layer 6 as the turn's counter history dictates, and it survives the counters later being
 *    removed (the card asks what you *put on* him this turn, not what is on him now).
 *  - **Fidelity caveat:** `SourceReceivedCounterThisTurn` matches a counter of *any* kind,
 *    while the card says "+1/+1 counters" specifically. Every counter this set can put on Beast
 *    is a +1/+1 counter, so the two readings coincide in practice — but a stun/shield/oil
 *    counter from outside the set would switch flying on where the printed card would not.
 *    Tightening it needs a counter-type parameter on that condition, i.e. new SDK vocabulary.
 */
val BeastEruditeAerialist = card("Beast, Erudite Aerialist") {
    manaCost = "{3}{G/U}"
    colorIdentity = "UG"
    typeLine = "Legendary Creature — Mutant Scientist Hero"
    power = 3
    toughness = 3
    oracleText = "As long as you've put one or more +1/+1 counters on Beast this turn, he has flying.\n" +
        "Whenever Beast deals combat damage to a player, draw a card."

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.FLYING, Filters.Self),
            condition = Conditions.SourceReceivedCounterThisTurn,
        )
    }

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.DrawCards(1)
        description = "Whenever Beast deals combat damage to a player, draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "206"
        artist = "Bachzim"
        flavorText = "\"Oh, my stars and garters!\""
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a92a95d2-9529-417a-b7d5-b4244d7fdca7.jpg?1783902905"
    }
}
