package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sphinx of Magosi
 * {3}{U}{U}{U}
 * Creature — Sphinx
 * 6 / 6
 *
 * Flying
 * {2}{U}: Draw a card, then put a +1/+1 counter on this creature.
 *
 * Modeling notes:
 *  - The ability is a plain [activatedAbility] with only a mana cost — no `{T}`, no timing
 *    restriction — so it is repeatable at instant speed for as much mana as is available. That is
 *    Assay's single `CostAtomWrapper` → `AtomMana("{2}{U}")`.
 *  - "Draw a card, **then** put a +1/+1 counter on this creature" is one effect with an ordered
 *    pair of steps, not two abilities: [Effects.Composite] in the printed order, mirroring Assay's
 *    `Composite` of `DrawCards` then `AddCounters`. The order is load-bearing — the card is drawn
 *    before the counter goes on, so a replacement that stops the draw does not stop the counter.
 *  - The counter goes on the Sphinx itself, so the target is [EffectTarget.Self] (Assay's
 *    `"target": {"type": "Self"}`), not a chosen target.
 */
val SphinxOfMagosi = card("Sphinx of Magosi") {
    manaCost = "{3}{U}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Sphinx"
    power = 6
    toughness = 6
    oracleText = "Flying\n" +
            "{2}{U}: Draw a card, then put a +1/+1 counter on this creature."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{2}{U}")
        effect = Effects.Composite(
            Effects.DrawCards(1),
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "89"
        artist = "James Ryman"
        flavorText = "\"A riddle is nothing more than a trap for small minds, baited with the promise of understanding.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/f/eff88609-f55e-45c5-be10-087d88789a83.jpg?1783941991"
    }
}
