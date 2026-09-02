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
 * Leave No Trace
 * {1}{W}
 * Instant
 *
 * Radiance — Destroy target enchantment and each other enchantment that shares a color with it.
 *
 * Radiance over enchantments: the target is destroyed directly; every *other* enchantment
 * sharing a color with it (`sharingColorWith(EntityReference.Target(0))`, `otherThanTarget()`)
 * is found as the spell resolves and destroyed too. A colorless enchantment shares a color with
 * nothing, so only it is destroyed.
 */
val LeaveNoTrace = card("Leave No Trace") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Radiance — Destroy target enchantment and each other enchantment that shares " +
        "a color with it."

    spell {
        val radiant = target("target enchantment", Targets.Enchantment)
        effect = Effects.Destroy(radiant) then
            Patterns.Group.destroyAll(
                GroupFilter(
                    GameObjectFilter.Enchantment.sharingColorWith(EntityReference.Target(0))
                ).otherThanTarget()
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "23"
        artist = "Pat Lee"
        flavorText = "The magic of the Boros patrols the streets even when their soldiers do not."
        imageUri = "https://cards.scryfall.io/normal/front/5/8/58466d46-7225-42ff-8471-6d489be32cf3.jpg?1783943698"
    }
}
