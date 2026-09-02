package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Rally the Righteous
 * {1}{R}{W}
 * Instant
 *
 * Radiance — Untap target creature and each other creature that shares a color with it. Those
 * creatures get +2/+0 until end of turn.
 *
 * Radiance: the target is untapped and pumped directly; every *other* creature sharing a color
 * with it (`sharingColorWith(EntityReference.Target(0))`, `otherThanTarget()`) is gathered once
 * as the spell resolves and untapped-then-pumped in the same pass — "those creatures" is the
 * group the first sentence named, so it isn't re-evaluated for the pump. A colorless target
 * shares a color with nothing, so only it is affected.
 */
val RallyTheRighteous = card("Rally the Righteous") {
    manaCost = "{1}{R}{W}"
    colorIdentity = "RW"
    typeLine = "Instant"
    oracleText = "Radiance — Untap target creature and each other creature that shares a color " +
        "with it. Those creatures get +2/+0 until end of turn."

    spell {
        val radiant = target("target creature", Targets.Creature)
        effect = Effects.Untap(radiant) then
            Effects.ModifyStats(2, 0, radiant) then
            Effects.ForEachInGroup(
                filter = GroupFilter(
                    GameObjectFilter.Creature.sharingColorWith(EntityReference.Target(0))
                ).otherThanTarget(),
                effect = Effects.Untap(EffectTarget.Self) then
                    Effects.ModifyStats(2, 0, EffectTarget.Self)
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "222"
        artist = "Dan Murayama Scott"
        flavorText = "Yuri took up the ragged Boros banner, and his brethren, inspired by the act, " +
            "followed him back into the fight."
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9da51b6c-2e27-4b41-8fb0-bd9f6ad47b19.jpg?1783943614"
    }
}
