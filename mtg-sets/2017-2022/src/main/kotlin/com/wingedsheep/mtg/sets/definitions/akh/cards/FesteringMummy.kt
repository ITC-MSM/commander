package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Festering Mummy
 * {B}
 * Creature — Zombie
 * 1/1
 * When this creature dies, you may put a -1/-1 counter on target creature.
 *
 * The printed "you may" is the builder's `optional = true`, which lowers into the same
 * `Gate.MayDecide` consent gate a hand-written `MayEffect` would build — the target is still
 * chosen as the ability goes on the stack (CR 603.3d), the yes/no is asked at resolution.
 */
val FesteringMummy = card("Festering Mummy") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    oracleText = "When this creature dies, you may put a -1/-1 counter on target creature."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.Dies
        optional = true
        val t = target("target", Targets.Creature)
        effect = Effects.AddCounters(Counters.MINUS_ONE_MINUS_ONE, 1, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "91"
        artist = "Christopher Burdett"
        flavorText = "As its parched flesh withers away, its malignant hunger grows."
        imageUri = "https://cards.scryfall.io/normal/front/9/0/906c4c95-5815-44c8-8d3c-b0fda9db55a1.jpg?1783936508"
    }
}
