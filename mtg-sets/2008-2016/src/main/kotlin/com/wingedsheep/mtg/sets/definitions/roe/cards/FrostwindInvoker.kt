package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Frostwind Invoker
 * {4}{U}
 * Creature — Merfolk Wizard
 * 3 / 3
 *
 * Flying
 * {8}: Creatures you control gain flying until end of turn.
 *
 * Modeling notes:
 *  - The invoker ability is a plain [activatedAbility] with a `{8}` mana cost — no tap symbol, no
 *    timing restriction — so it is repeatable and usable at instant speed.
 *  - "Creatures you control" is a **group**, not a target: [Effects.ForEachInGroup] over
 *    [GroupFilter.AllCreaturesYouControl] grants the keyword once per member, with
 *    [EffectTarget.Self] naming the current iteration entity. That matches Assay's `ForEach` over
 *    an `IterationSpace.Group` whose filter is `IsCreature` + `ControlledByYou`.
 *  - The group is evaluated on resolution, so creatures that arrive later this turn do not gain
 *    flying — which is what "creatures you control gain flying until end of turn" says (a one-shot
 *    grant, not a continuous "have flying" static).
 *  - [Effects.GrantKeyword] already defaults to an end-of-turn duration, so no explicit `duration`
 *    is written.
 */
val FrostwindInvoker = card("Frostwind Invoker") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    power = 3
    toughness = 3
    oracleText = "Flying\n" +
        "{8}: Creatures you control gain flying until end of turn."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{8}")
        effect = Effects.ForEachInGroup(
            GroupFilter.AllCreaturesYouControl,
            Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "68"
        artist = "Svetlin Velinov"
        flavorText = "\"We thought Zendikar's rage was kindled by its explorers and plunderers. But the world had sensed the stirrings of the Eldrazi.\"\n—*The Invokers' Tales*"
        imageUri = "https://cards.scryfall.io/normal/front/6/6/66ec9a28-3b36-4b6a-b420-7b4266b64f69.jpg?1783941996"
    }
}
