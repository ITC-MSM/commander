package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Bramblesnap
 * {1}{G}
 * Creature — Elemental
 * 1 / 1
 *
 * Trample
 * Tap an untapped creature you control: This creature gets +1/+1 until end of turn.
 *
 * Modeling notes:
 *  - Trample is a live keyword the combat code reads, so the printed line is just
 *    `keywords(Keyword.TRAMPLE)`.
 *  - The cost taps *another permanent*, it is not the `{T}` symbol: [Costs.TapPermanents] with
 *    `count = 1`. Two consequences follow from that distinction — summoning sickness does not gate
 *    the tapped creature (CR 302.6 applies only to `{T}`/`{Q}` in the cost of the source's own
 *    ability, and the cost atom taps a *chosen* permanent, CR 701.26a), and Bramblesnap itself need
 *    not be untapped to activate.
 *  - The printed text does **not** say "another", so `excludeSelf` stays at its `false` default:
 *    an untapped Bramblesnap is a legal choice for its own cost, taking itself out of combat to buy
 *    +1/+1. Contrast `Costs.TapAnotherPermanent`, which is the wording this card avoids.
 *  - **Divergence from Assay's compiled JSON, deliberately.** Assay renders the cost filter as bare
 *    `IsCreature`, dropping the printed "you control". The tap-cost atom's candidate domain is
 *    already `controlledUntapped(...)` — payer-scoped and untapped-only — so the two spellings are
 *    behaviourally identical and Assay is merely under-specifying rather than saying something
 *    different. The printed restriction is written out anyway as `GameObjectFilter.Creature
 *    .youControl()`, matching Honeymoon Hearse and Dragonbrood's Relic, so that the card reads the
 *    way it is printed and stays correct if the atom's domain is ever widened.
 *  - "gets +1/+1 until end of turn" is [Effects.ModifyStats] on [EffectTarget.Self]; its `duration`
 *    already defaults to `EndOfTurn`, so no explicit duration is written.
 */
val Bramblesnap = card("Bramblesnap") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elemental"
    power = 1
    toughness = 1
    oracleText = "Trample\n" +
            "Tap an untapped creature you control: This creature gets +1/+1 until end of turn."

    keywords(Keyword.TRAMPLE)

    activatedAbility {
        cost = Costs.TapPermanents(count = 1, filter = GameObjectFilter.Creature.youControl())
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
        description = "Tap an untapped creature you control: This creature gets +1/+1 until end of turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "180"
        artist = "James Ryman"
        flavorText = "A bramblesnap is formed by grafting together thirteen different plants that already hunger for meat."
        imageUri = "https://cards.scryfall.io/normal/front/d/c/dcddd71f-bb8d-4153-854f-af87189babe7.jpg?1783941966"
    }
}
