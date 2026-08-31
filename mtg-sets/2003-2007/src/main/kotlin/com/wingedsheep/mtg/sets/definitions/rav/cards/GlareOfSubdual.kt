package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.TapUntapEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Glare of Subdual — Ravnica: City of Guilds #207 (canonical printing; reprinted in EMA and GK1)
 * {2}{G}{W} · Enchantment
 *
 * Tap an untapped creature you control: Tap target artifact or creature.
 *
 * A repeatable tapper whose only cost is a creature tap, so it is one `activatedAbility` with
 * `Costs.TapPermanents(1, Creature)` and nothing else — no mana, and no `{T}` of its own (the
 * enchantment never taps).
 *
 * The cost atom already carries the two restrictions the printed cost states: the creature must be
 * **untapped** and **yours**, both intrinsic to `CostAtom.TapPermanents`. Summoning sickness does
 * *not* apply — this is not a `{T}` cost of the creature's own ability, so a creature that came
 * down this turn can pay (the Convoke rule). The ability has no activation restriction, so it works
 * at instant speed and any number of times, once per untapped creature.
 */
val GlareOfSubdual = card("Glare of Subdual") {
    manaCost = "{2}{G}{W}"
    colorIdentity = "GW"
    typeLine = "Enchantment"
    oracleText = "Tap an untapped creature you control: Tap target artifact or creature."

    activatedAbility {
        cost = Costs.TapPermanents(1, GameObjectFilter.Creature)
        val t = target(
            "target artifact or creature",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.Artifact or GameObjectFilter.Creature)),
        )
        effect = TapUntapEffect(target = t, tap = true)
        description = "Tap an untapped creature you control: Tap target artifact or creature."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "207"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "The righteous light of Selesnya is channeled through the devout, striking out to blind the nonbelievers."
        imageUri = "https://cards.scryfall.io/normal/front/e/d/ed6166c1-3c2e-47af-873e-d3b39f42bd27.jpg?1783943621"
    }
}
