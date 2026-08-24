package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rally
 * {W}{W}
 * Instant
 *
 * Blocking creatures get +1/+1 until end of turn.
 *
 * The untargeted group pump — Trumpet Blast's shape with `blocking()` in place of `attacking()`, so
 * the set of creatures is decided by combat state at resolution rather than by a target chosen on
 * announcement.
 */
val Rally = card("Rally") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Blocking creatures get +1/+1 until end of turn."

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.blocking()),
            Effects.ModifyStats(1, 1, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "48"
        artist = "Heather Hudson"
        flavorText = "\"Stand your ground, troops! This shall be our finest hour!\"\n—General Jarkeld, the Arctic Fox, last words"
        imageUri = "https://cards.scryfall.io/normal/front/e/1/e1e9f80e-5d75-45b7-9c66-c0f30996f4dc.jpg"
    }
}
