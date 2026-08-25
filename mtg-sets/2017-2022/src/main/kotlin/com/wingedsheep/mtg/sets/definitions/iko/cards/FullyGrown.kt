package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fully Grown
 * {2}{G}
 * Instant
 * Target creature gets +3/+3 until end of turn. Put a trample counter on it.
 *
 * Two halves with different lifetimes on one target: the pump is a floating end-of-turn stat
 * modifier, while the trample counter is a keyword counter (CR 122.1b / 613.1f) and so stays on
 * the creature permanently.
 */
val FullyGrown = card("Fully Grown") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature gets +3/+3 until end of turn. Put a trample counter on it."

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(3, 3, t),
            Effects.AddCounters(Counters.TRAMPLE, 1, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "154"
        artist = "Dmitry Burmak"
        flavorText = "\"One day I woke up and knew I had nothing left to teach her. So now I ride on her shoulders and follow where she leads.\"\n—Gustin, wildbonder"
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0b683d3f-025c-4b8d-89d7-3513488649d5.jpg"
    }
}
