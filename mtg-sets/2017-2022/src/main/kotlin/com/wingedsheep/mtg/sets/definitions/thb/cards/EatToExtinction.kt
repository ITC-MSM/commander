package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Eat to Extinction
 * {3}{B}
 * Instant
 *
 * Exile target creature or planeswalker. Surveil 1. (Look at the top card of your library. You may
 * put that card into your graveyard.)
 *
 * [Effects.Exile] is the exile-flavoured `MoveToZone`; no `fromZone` guard, because the target is a
 * battlefield permanent chosen at cast time rather than a card fished out of another zone. The
 * surveil rider is the compact [Effects.Surveil] macro, which the engine expands to the shared
 * surveil pipeline at resolution so "whenever you surveil" triggers still see it.
 */
val EatToExtinction = card("Eat to Extinction") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Exile target creature or planeswalker. Surveil 1. (Look at the top card of your " +
        "library. You may put that card into your graveyard.)"

    spell {
        val t = target("target", Targets.CreatureOrPlaneswalker)
        effect = Effects.Composite(
            Effects.Exile(t),
            Effects.Surveil(1)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "90"
        artist = "Vincent Proce"
        flavorText = "\"Kroxa devours what he may—not for sustenance or pleasure, but because it is " +
            "his nature. He is unending hunger given form.\"\n—Klothys, god of destiny"
        imageUri = "https://cards.scryfall.io/normal/front/a/0/a0fae55a-6edd-42ea-b909-ccc39a64a0ed.jpg"
    }
}
