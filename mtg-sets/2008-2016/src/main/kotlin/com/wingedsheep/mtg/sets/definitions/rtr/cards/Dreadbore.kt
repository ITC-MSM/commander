package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Dreadbore
 * {B}{R}
 * Sorcery
 *
 * Destroy target creature or planeswalker.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * [Targets.CreatureOrPlaneswalker] is one requirement over both types rather than two targets —
 * the card chooses a single object.
 */
val Dreadbore = card("Dreadbore") {
    manaCost = "{B}{R}"
    colorIdentity = "BR"
    typeLine = "Sorcery"
    oracleText = "Destroy target creature or planeswalker."

    spell {
        val t = target("target creature or planeswalker", Targets.CreatureOrPlaneswalker)
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "157"
        artist = "Wayne Reynolds"
        flavorText = "In Rakdos-controlled neighborhoods, everyone is part of the show."
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a83945c6-4dc6-4d9a-9bc2-2d4a264e5422.jpg?1783940341"
    }
}
