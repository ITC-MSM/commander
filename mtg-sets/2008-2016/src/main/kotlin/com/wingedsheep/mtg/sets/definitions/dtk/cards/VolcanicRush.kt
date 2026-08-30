package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Volcanic Rush
 * {4}{R}
 * Instant
 *
 * Attacking creatures get +2/+0 and gain trample until end of turn.
 *
 * "Attacking creatures" is an unqualified group, not a target — every attacker on the battlefield,
 * whoever controls it — so this is [Effects.ForEachInGroup] over [GroupFilter.AttackingCreatures]
 * with [EffectTarget.Self] naming the current iteration's creature. The group is snapshotted before
 * the first iteration, so a creature that leaves combat mid-resolution still gets its bonus.
 */
val VolcanicRush = card("Volcanic Rush") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Attacking creatures get +2/+0 and gain trample until end of turn."

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter.AttackingCreatures,
            Effects.Composite(
                Effects.ModifyStats(2, 0, EffectTarget.Self),
                Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self)
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "166"
        artist = "Ryan Barger"
        flavorText = "\"The bravest warriors take the shortest path to victory, whatever that path may be.\"\n—Sakta, Atarka hunter"
        imageUri = "https://cards.scryfall.io/normal/front/7/b/7b808883-f630-4070-ab7e-d347e26a9564.jpg?1783938584"
    }
}
