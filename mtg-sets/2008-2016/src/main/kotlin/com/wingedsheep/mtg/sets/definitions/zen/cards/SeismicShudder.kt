package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Seismic Shudder
 * {1}{R}
 * Instant
 * Seismic Shudder deals 1 damage to each creature without flying.
 *
 * Damage to *each* matching creature is a group iteration with [EffectTarget.Self] as the body's
 * recipient — the iteration binds "self" to the creature being visited, not to the spell.
 */
val SeismicShudder = card("Seismic Shudder") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Seismic Shudder deals 1 damage to each creature without flying."

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.withoutKeyword(Keyword.FLYING)),
            Effects.DealDamage(1, EffectTarget.Self),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "147"
        artist = "Vincent Proce"
        flavorText = "\"The land here seems to go out of its way to kill you.\"\n—Chandra Nalaar"
        imageUri = "https://cards.scryfall.io/normal/front/2/0/20365082-6102-4e3b-8791-c9b66846270d.jpg"
    }
}
