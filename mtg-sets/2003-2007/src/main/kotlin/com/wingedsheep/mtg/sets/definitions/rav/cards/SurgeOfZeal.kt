package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Surge of Zeal
 * {R}
 * Instant
 *
 * Radiance — Target creature and each other creature that shares a color with it gain haste
 * until end of turn.
 *
 * Radiance: the target gains haste directly; every *other* creature sharing a color with it
 * (`sharingColorWith(EntityReference.Target(0))`, `otherThanTarget()`) is found as the spell
 * resolves and gains haste too. A colorless target shares a color with nothing, so only it is
 * affected.
 */
val SurgeOfZeal = card("Surge of Zeal") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Radiance — Target creature and each other creature that shares a color with it " +
        "gain haste until end of turn."

    spell {
        val radiant = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.HASTE, radiant) then
            Patterns.Group.grantKeywordToAll(
                Keyword.HASTE,
                GroupFilter(
                    GameObjectFilter.Creature.sharingColorWith(EntityReference.Target(0))
                ).otherThanTarget()
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "146"
        artist = "Justin Sweet"
        flavorText = "\"If only my poxes were as infectious as their zealotry.\"\n—Savra"
        imageUri = "https://cards.scryfall.io/normal/front/b/6/b6c07fa6-b575-46f9-ab39-fb777d4b8acd.jpg?1783943646"
    }
}
