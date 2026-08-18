package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Riptide
 * {U}
 * Instant
 * Tap all blue creatures.
 *
 * Every blue creature on the battlefield, including the caster's own — already-tapped ones are
 * simply unaffected.
 */
val Riptide = card("Riptide") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Tap all blue creatures."

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.withColor(Color.BLUE)),
            Effects.Tap(EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "35"
        artist = "Randy Asplund-Faith"
        imageUri = "https://cards.scryfall.io/normal/front/b/0/b0f11ae4-e30e-441d-bb64-439930d9997c.jpg?1783947941"
    }
}
