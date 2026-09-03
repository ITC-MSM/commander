package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Wojek Apothecary
 * {2}{W}{W}
 * Creature — Human Cleric
 * 1/1
 *
 * Radiance — {T}: Prevent the next 1 damage that would be dealt to target creature and each other
 * creature that shares a color with it this turn.
 *
 * Radiance with a prevention shield instead of damage: the target gets its own shield directly,
 * and every *other* creature sharing a color with it (`sharingColorWith(EntityReference.Target(0))`,
 * `otherThanTarget()`) is found as the ability resolves and gets a shield of its own. Each shield
 * is per-creature — 1 damage prevented on each, not 1 across the group — which is why this is a
 * `ForEachInGroup` around `PreventNextDamage` rather than a group-scoped prevention. A colorless
 * target shares a color with nothing, so only it is shielded.
 *
 * Scryfall lists a `psal` (Salvat 2005) printing three weeks before Ravnica, but that is a regional
 * box product rather than a real expansion, so the canonical printing stays RAV — the same call
 * Selesnya Sanctuary and Seed Spark document.
 */
val WojekApothecary = card("Wojek Apothecary") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Cleric"
    oracleText = "Radiance — {T}: Prevent the next 1 damage that would be dealt to target creature " +
        "and each other creature that shares a color with it this turn."
    power = 1
    toughness = 1

    activatedAbility {
        cost = Costs.Tap
        val patient = target("target creature", Targets.Creature)
        effect = Effects.PreventNextDamage(1, patient) then
            Effects.ForEachInGroup(
                GroupFilter(
                    GameObjectFilter.Creature.sharingColorWith(EntityReference.Target(0))
                ).otherThanTarget(),
                Effects.PreventNextDamage(1, EffectTarget.Self)
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "36"
        artist = "Keith Garletts"
        flavorText = "\"A few arrows aren't enough to pierce your faith, soldier. You'll be back " +
            "on the battlefield by sun's dawn.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/f/ef74fea5-2345-4ffd-8951-f420e17259e6.jpg?1783943692"
    }
}
