package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Guiding Voice — Strixhaven: School of Mages #19 (canonical printing)
 * {W} · Sorcery
 *
 * Put a +1/+1 counter on target creature.
 * Learn.
 *
 * The counter is not optional and not restricted to your own creatures — "target creature",
 * so it can be handed to an opponent's, which matters only as a rules detail here.
 *
 * `Learn` is [Patterns.Mechanic.learn] (CR 701.48). The clauses resolve in printed order, so the
 * Learn happens after the counter regardless of whether the creature is still there.
 */
val GuidingVoice = card("Guiding Voice") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Put a +1/+1 counter on target creature.\n" +
        "Learn. (You may reveal a Lesson card you own from outside the game and put it into your " +
        "hand, or discard a card to draw a card.)"

    spell {
        val creature = target("target creature", Targets.Creature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature) then
            Patterns.Mechanic.learn()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "19"
        artist = "Steve Argyle"
        flavorText = "When honormancers work together, their compliments are complementary."
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d6fb3163-12ca-4a7f-a0c7-b8ddfc9408a0.jpg?1783927388"
    }
}
