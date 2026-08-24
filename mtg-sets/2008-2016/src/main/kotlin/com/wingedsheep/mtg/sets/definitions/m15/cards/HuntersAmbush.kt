package com.wingedsheep.mtg.sets.definitions.m15.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Hunter's Ambush
 * {2}{G}
 * Instant
 * Prevent all combat damage that would be dealt by nongreen creatures this turn.
 *
 * A source-side shield ([Effects.PreventCombatDamageFrom]) — the group is re-evaluated against
 * projected state as each damage instance would be dealt, so a creature that becomes green
 * mid-turn deals its damage normally.
 */
val HuntersAmbush = card("Hunter's Ambush") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Prevent all combat damage that would be dealt by nongreen creatures this turn."

    spell {
        effect = Effects.PreventCombatDamageFrom(
            GroupFilter(GameObjectFilter.Creature.notColor(Color.GREEN))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "180"
        artist = "David Palumbo"
        flavorText = "First you lose your enemy's trail. Then you lose all sense of direction. Then you hear the growls . . ."
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d2971049-b916-4b76-b18f-8650d8d2545d.jpg?1783939166"
    }
}
