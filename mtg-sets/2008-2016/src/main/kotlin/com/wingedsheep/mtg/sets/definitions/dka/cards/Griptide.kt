package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ZonePlacement

/**
 * Griptide
 * {3}{U}
 * Instant
 *
 * Put target creature on top of its owner's library.
 *
 * A plain move to the top of the library — "its owner's" is not a knob, since a card always leaves
 * the battlefield for its owner's zone (CR 400.3). Totally Lost (GTC) is the same shape.
 */
val Griptide = card("Griptide") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Put target creature on top of its owner's library."

    spell {
        val victim = target("target", Targets.Creature)
        effect = Effects.Move(victim, Zone.LIBRARY, ZonePlacement.Top)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "38"
        artist = "Igor Kieryluk"
        flavorText = "\"Beware the seagrafs just off the shore. These waters are filled with hungry geists looking for an easy meal.\"\n—Captain Eberhart"
        imageUri = "https://cards.scryfall.io/normal/front/2/7/27f92b74-86bb-4bb3-8f78-640984698f28.jpg"
    }
}
