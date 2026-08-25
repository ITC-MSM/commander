package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Stronghold Overseer
 * {3}{B}{B}{B}
 * Creature — Demon
 * 5/5
 * Flying
 * Shadow (This creature can block or be blocked by only creatures with shadow.)
 * {B}{B}: Creatures with shadow get +1/+0 until end of turn and creatures without shadow get
 * -1/-0 until end of turn.
 *
 * One activation, two board-wide pumps in opposite directions — neither names a controller, so
 * both halves are [GroupFilter]s over every creature, split by the shadow keyword. Each half is an
 * [Effects.ForEachInGroup] whose body targets [EffectTarget.Self], i.e. the current iteration
 * entity; the Overseer's own shadow puts it in the +1/+0 half.
 */
val StrongholdOverseer = card("Stronghold Overseer") {
    manaCost = "{3}{B}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Demon"
    power = 5
    toughness = 5
    oracleText = "Flying\n" +
        "Shadow (This creature can block or be blocked by only creatures with shadow.)\n" +
        "{B}{B}: Creatures with shadow get +1/+0 until end of turn and creatures without shadow get -1/-0 until end of turn."

    keywords(Keyword.FLYING, Keyword.SHADOW)

    activatedAbility {
        cost = Costs.Mana("{B}{B}")
        effect = Effects.Composite(
            Effects.ForEachInGroup(
                GroupFilter(GameObjectFilter.Creature.withKeyword(Keyword.SHADOW)),
                Effects.ModifyStats(1, 0, EffectTarget.Self)
            ),
            Effects.ForEachInGroup(
                GroupFilter(GameObjectFilter.Creature.withoutKeyword(Keyword.SHADOW)),
                Effects.ModifyStats(-1, 0, EffectTarget.Self)
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "133"
        artist = "Puddnhead"
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e0722fa2-53df-4217-a592-fcaf239f717a.jpg"
    }
}
