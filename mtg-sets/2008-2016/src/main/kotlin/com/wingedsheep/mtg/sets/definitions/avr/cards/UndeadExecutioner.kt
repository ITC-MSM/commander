package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Undead Executioner — Avacyn Restored #123
 * {3}{B} · Creature — Zombie · 2/2
 *
 * When this creature dies, you may have target creature get -2/-2 until end of turn.
 *
 * The printed "you may" is the builder's `optional`, which lowers to a `Gate.MayDecide` around the
 * pump — the target is still locked when the trigger goes on the stack (CR 603.3d) and only the
 * yes/no waits for resolution.
 */
val UndeadExecutioner = card("Undead Executioner") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    power = 2
    toughness = 2
    oracleText = "When this creature dies, you may have target creature get -2/-2 until end of turn."

    triggeredAbility {
        trigger = Triggers.Dies
        optional = true
        val t = target("target", Targets.Creature)
        effect = Effects.ModifyStats(-2, -2, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "123"
        artist = "Dave Kendall"
        flavorText = "Heartless killer in life, brainless killer in death."
        imageUri = "https://cards.scryfall.io/normal/front/8/d/8d330058-16af-4486-aa89-b6be759e35d4.jpg?1783940690"
    }
}
