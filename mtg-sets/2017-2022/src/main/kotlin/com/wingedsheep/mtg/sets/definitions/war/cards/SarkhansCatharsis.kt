package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sarkhan's Catharsis — War of the Spark #144 (canonical printing)
 * {4}{R}
 * Instant
 * Sarkhan's Catharsis deals 5 damage to target player or planeswalker.
 *
 * "Target player or planeswalker" is its own target requirement, not "any target" minus
 * creatures — [Targets.PlayerOrPlaneswalker].
 */
val SarkhansCatharsis = card("Sarkhan's Catharsis") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Sarkhan's Catharsis deals 5 damage to target player or planeswalker."

    spell {
        val victim = target("target", Targets.PlayerOrPlaneswalker)
        effect = Effects.DealDamage(5, victim)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "144"
        artist = "Zack Stella"
        flavorText = "\"You once brought havoc to my home and ruined my mind. I'm here to return the favor, Bolas.\""
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2f4b6f26-c66b-4048-9503-af0a886ef14f.jpg"
    }
}
