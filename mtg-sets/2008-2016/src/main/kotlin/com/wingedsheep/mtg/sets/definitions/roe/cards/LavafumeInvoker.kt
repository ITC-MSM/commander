package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Lavafume Invoker
 * {2}{R}
 * Creature — Goblin Shaman
 * 2 / 2
 *
 * {8}: Creatures you control get +3/+0 until end of turn.
 *
 * Modeling notes:
 *  - The invoker ability is a plain [activatedAbility] with a `{8}` mana cost — no tap symbol, no
 *    timing restriction — so it is repeatable and usable at instant speed.
 *  - "Creatures you control" is a **group**, not a target: [Effects.ForEachInGroup] over
 *    [GroupFilter.AllCreaturesYouControl] applies the pump once per member, with
 *    [EffectTarget.Self] naming the current iteration entity. That is Assay's `ForEach` over an
 *    `IterationSpace.Group` filtered on `IsCreature` + `ControlledByYou`.
 *  - The toughness modifier is written as an explicit `0` because the printed line is `+3/+0`, and
 *    the group is snapshotted on resolution — creatures that arrive later this turn are not pumped.
 *  - [Effects.ModifyStats] already defaults to an end-of-turn duration, so no `duration` is written.
 */
val LavafumeInvoker = card("Lavafume Invoker") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Shaman"
    power = 2
    toughness = 2
    oracleText = "{8}: Creatures you control get +3/+0 until end of turn."

    activatedAbility {
        cost = Costs.Mana("{8}")
        effect = Effects.ForEachInGroup(
            GroupFilter.AllCreaturesYouControl,
            Effects.ModifyStats(3, 0, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "155"
        artist = "Dave Kendall"
        flavorText = "\"Then the ancient masters themselves, towers of rapacity, rose and began their calamitous feast.\"\n—*The Invokers' Tales*"
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8e773b3f-37ef-4e37-8b1e-99b7b6314877.jpg?1783941974"
    }
}
