package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Tanglesap
 * {1}{G}
 * Instant
 * Prevent all combat damage that would be dealt this turn by creatures without trample.
 *
 * A filtered Fog: the shield is keyed to the damage *source*, and the filter is re-evaluated
 * against projected state at the moment damage would be dealt — a creature that gains trample
 * after Tanglesap resolves still connects.
 */
val Tanglesap = card("Tanglesap") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Prevent all combat damage that would be dealt this turn by creatures without trample."

    spell {
        effect = Effects.PreventCombatDamageFrom(
            GroupFilter(GameObjectFilter.Creature.withoutKeyword(Keyword.TRAMPLE))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "186"
        artist = "Trevor Claxton"
        flavorText = "\"The blood of the forest is beholden to no one.\"\n—Nissa Revane"
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a3c4e15b-7460-4d61-a209-6de27e4df6bb.jpg"
    }
}
