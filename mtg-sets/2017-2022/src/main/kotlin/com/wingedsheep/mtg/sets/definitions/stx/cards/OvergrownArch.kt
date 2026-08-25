package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Overgrown Arch — Strixhaven: School of Mages #139 (canonical printing)
 * {1}{G} · Creature — Plant Wall · 0/4
 *
 * Defender
 * {T}: You gain 1 life.
 * {2}, Sacrifice this creature: Learn.
 *
 * A blocker that cashes itself in for a Lesson once it has stopped mattering. The Learn is on an
 * *activated* ability rather than a dies trigger, so it only happens when you pay for it —
 * killing the Arch in combat gets the opponent nothing.
 *
 * Note both abilities are usable the turn it enters: [Costs.SacrificeSelf] is not a tap cost, so
 * summoning sickness does not gate it — only the separate `{T}` lifegain ability is gated.
 *
 * `Learn` is [Patterns.Mechanic.learn] (CR 701.48).
 */
val OvergrownArch = card("Overgrown Arch") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant Wall"
    power = 0
    toughness = 4
    oracleText = "Defender\n" +
        "{T}: You gain 1 life.\n" +
        "{2}, Sacrifice this creature: Learn. (You may reveal a Lesson card you own from outside " +
        "the game and put it into your hand, or discard a card to draw a card.)"

    keywords(Keyword.DEFENDER)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.GainLife(1)
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.SacrificeSelf
        )
        effect = Patterns.Mechanic.learn()
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "139"
        artist = "Simon Dominic"
        imageUri = "https://cards.scryfall.io/normal/front/a/7/a71dddf5-8f72-4018-9cc8-5f63a17f1ee5.jpg?1783927339"
    }
}
