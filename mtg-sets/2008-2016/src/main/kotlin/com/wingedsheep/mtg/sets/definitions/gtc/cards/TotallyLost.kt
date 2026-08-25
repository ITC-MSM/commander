package com.wingedsheep.mtg.sets.definitions.gtc.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ZonePlacement

/**
 * Totally Lost
 * {4}{U}
 * Instant
 * Put target nonland permanent on top of its owner's library.
 *
 * A plain move to the top of the library — "its owner's" is not a knob, since a card always leaves
 * the battlefield for its owner's zone (CR 400.3).
 */
val TotallyLost = card("Totally Lost") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Put target nonland permanent on top of its owner's library."

    spell {
        val victim = target("target", Targets.NonlandPermanent)
        effect = Effects.Move(victim, Zone.LIBRARY, ZonePlacement.Top)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "54"
        artist = "David Palumbo"
        flavorText = "Fblthp had always hated crowds."
        imageUri = "https://cards.scryfall.io/normal/front/e/c/ec8e4142-7c46-4d2f-aaa6-6410f323d9f0.jpg"
    }
}
