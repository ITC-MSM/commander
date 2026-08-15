package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Stun
 * {1}{R}
 * Instant
 * Target creature can't block this turn.
 * Draw a card.
 *
 * Canonical printing: Tempest (earliest real-expansion printing). Reprinted in
 * Invasion, Tenth Edition, and Tempest Remastered.
 */
val Stun = card("Stun") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Target creature can't block this turn.\nDraw a card."

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.CantBlock(t)
            .then(Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "207"
        artist = "Terese Nielsen"
        flavorText = "\"I concede it was a cheap shot, but it was the only one I could afford.\"\n—Gerrard of the *Weatherlight*"
        imageUri = "https://cards.scryfall.io/normal/front/c/0/c09c0da6-37a7-42ba-b264-18898ee372f0.jpg?1562056397"
    }
}
