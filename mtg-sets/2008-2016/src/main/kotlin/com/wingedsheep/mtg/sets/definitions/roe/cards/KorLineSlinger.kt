package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Kor Line-Slinger
 * {1}{W}
 * Creature — Kor Scout
 * 0 / 1
 *
 * {T}: Tap target creature with power 3 or less.
 *
 * Modeling notes:
 *  - The whole cost is the tap symbol — [Costs.Tap], Assay's bare `CostTap` — so the ability is
 *    once per turn and subject to summoning sickness (CR 302.6), with no mana component.
 *  - "Target creature with power 3 or less" is [Targets.CreatureWithPowerAtMost], which builds the
 *    `Creature.powerAtMost(3)` filter — Assay's `IsCreature` + `PowerAtMost(max: 3)` pair. Power is
 *    read off *projected* state, so a creature pumped past 3 in response stops being a legal target
 *    and the ability fizzles.
 *  - The tap is [Effects.Tap] on the declared target; targeting an already-tapped creature is legal
 *    and simply does nothing, so no untapped restriction is written into the filter.
 */
val KorLineSlinger = card("Kor Line-Slinger") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kor Scout"
    power = 0
    toughness = 1
    oracleText = "{T}: Tap target creature with power 3 or less."

    activatedAbility {
        cost = Costs.Tap
        val creature = target("target creature with power 3 or less", Targets.CreatureWithPowerAtMost(3))
        effect = Effects.Tap(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "30"
        artist = "Steve Prescott"
        flavorText = "\"I tried to tell her to stay behind, that this fight was too dangerous. I spent the next hour tied to the rafters.\"\n—Zahr Gada, Halimar expedition leader"
        imageUri = "https://cards.scryfall.io/normal/front/0/6/068943d2-c456-42a3-8088-3e4923bf6d74.jpg?1783942006"
    }
}
