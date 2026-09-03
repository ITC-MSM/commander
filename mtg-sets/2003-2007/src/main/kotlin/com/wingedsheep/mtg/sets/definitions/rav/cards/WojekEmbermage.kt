package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Wojek Embermage
 * {3}{R}
 * Creature — Human Wizard
 * 1/2
 *
 * Radiance — {T}: This creature deals 1 damage to target creature and each other creature that
 * shares a color with it.
 *
 * The repeatable half of Cleansing Beam: one target, and a resolution-time group relative to it.
 * The target is damaged directly; the group is every *other* creature sharing a color with the
 * target — `sharingColorWith(EntityReference.Target(0))` for the colour test, `otherThanTarget()`
 * so the target isn't hit twice. The Embermage itself is red, so a red target catches it too;
 * "each other creature" is relative to the target, not to the source, and there is no
 * `excludeSelf` here. A colorless target shares a color with nothing, so only it is damaged.
 */
val WojekEmbermage = card("Wojek Embermage") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Wizard"
    oracleText = "Radiance — {T}: This creature deals 1 damage to target creature and each other " +
        "creature that shares a color with it."
    power = 1
    toughness = 2

    activatedAbility {
        cost = Costs.Tap
        val victim = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(1, victim) then
            Patterns.Group.dealDamageToAll(
                1,
                GroupFilter(
                    GameObjectFilter.Creature.sharingColorWith(EntityReference.Target(0))
                ).otherThanTarget()
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "152"
        artist = "Luca Zontini"
        flavorText = "\"Your brother's crimes are your crimes. You stood by and lent support, so " +
            "you too must face judgment.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/1/e1614866-fb0b-47bd-ab26-5c20ff175d10.jpg?1783943643"
    }
}
