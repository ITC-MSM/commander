package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Wojek Siren
 * {W}
 * Instant
 *
 * Radiance — Target creature and each other creature that shares a color with it get +1/+1
 * until end of turn.
 *
 * Radiance: the target is pumped directly; every *other* creature sharing a color with it
 * (`sharingColorWith(EntityReference.Target(0))`, `otherThanTarget()`) is found as the spell
 * resolves and pumped too. A colorless target shares a color with nothing, so only it grows.
 */
val WojekSiren = card("Wojek Siren") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Radiance — Target creature and each other creature that shares a color with it " +
        "get +1/+1 until end of turn."

    spell {
        val radiant = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(1, 1, radiant) then
            Patterns.Group.modifyStatsForAll(
                1,
                1,
                GroupFilter(
                    GameObjectFilter.Creature.sharingColorWith(EntityReference.Target(0))
                ).otherThanTarget()
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "37"
        artist = "Zoltan Boros & Gabor Szikszai"
        flavorText = "It is the call to arms, the call to fury, the call to blood."
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a55c0d7d-2325-4d7e-b449-c8fdcf988ec0.jpg?1783943692"
    }
}
