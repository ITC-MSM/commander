package com.wingedsheep.mtg.sets.definitions.m14.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ForceSacrificeEffect

/**
 * Celestial Flare
 * {W}{W}
 * Instant
 *
 * Target player sacrifices an attacking or blocking creature of their choice.
 *
 * An edict, not removal: [ForceSacrificeEffect] makes the *targeted player* choose, so the
 * attacking-or-blocking restriction is a filter on their choice rather than a target filter, and
 * hexproof or protection on the creature is irrelevant.
 */
val CelestialFlare = card("Celestial Flare") {
    manaCost = "{W}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target player sacrifices an attacking or blocking creature of their choice."

    spell {
        val p = target("target player", Targets.Player)
        effect = ForceSacrificeEffect(
            GameObjectFilter.Creature.attackingOrBlocking(),
            1,
            p
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "12"
        artist = "Clint Cearley"
        flavorText = "\"You were defeated the moment you declared your aggression.\"\n" +
            "—Gideon Jura"
        imageUri = "https://cards.scryfall.io/normal/front/6/c/6c8d1320-0f1a-4c66-86c9-9f8da0f1d9ef.jpg"
    }
}
