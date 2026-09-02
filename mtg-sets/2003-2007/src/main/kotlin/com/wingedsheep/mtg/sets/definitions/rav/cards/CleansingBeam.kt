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
 * Cleansing Beam
 * {4}{R}
 * Instant
 *
 * Radiance — Cleansing Beam deals 2 damage to target creature and each other creature that
 * shares a color with it.
 *
 * Radiance is an ability word: one target, and a resolution-time group relative to it. The
 * target is damaged directly; the group is every *other* creature sharing a color with the
 * target — `sharingColorWith(EntityReference.Target(0))` for the colour test, `otherThanTarget()`
 * so the target isn't hit twice. A colorless target shares a color with nothing (Scryfall ruling
 * 2005-10-01), so only the target is damaged. The group is checked as the spell resolves, and if
 * the target has become illegal the whole spell fizzles and no other creature is affected.
 */
val CleansingBeam = card("Cleansing Beam") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Radiance — Cleansing Beam deals 2 damage to target creature and each other " +
        "creature that shares a color with it."

    spell {
        val victim = target("target creature", Targets.Creature)
        effect = Effects.DealDamage(2, victim) then
            Patterns.Group.dealDamageToAll(
                2,
                GroupFilter(
                    GameObjectFilter.Creature.sharingColorWith(EntityReference.Target(0))
                ).otherThanTarget()
            )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "118"
        artist = "Pat Lee"
        flavorText = "\"Justice is toothless without punishment. Righteousness cannot succeed " +
            "without the suffering of the guilty.\"\n—Razia"
        imageUri = "https://cards.scryfall.io/normal/front/6/3/63cbaab6-db0d-40bb-bdf4-aee6543d9f27.jpg?1783943657"
        ruling("2005-10-01", "All creatures that share a color are affected, even your own.")
        ruling(
            "2005-10-01",
            "If it targets a colorless creature, it doesn't affect any other creatures. A colorless " +
                "creature shares a color with nothing, not even other colorless creatures."
        )
        ruling("2005-10-01", "You check which creatures share a color with the target when the spell resolves.")
        ruling(
            "2005-10-01",
            "Only one creature is targeted. If that creature leaves the battlefield or otherwise becomes " +
                "an illegal target, the entire spell doesn't resolve. No other creatures are affected."
        )
    }
}
