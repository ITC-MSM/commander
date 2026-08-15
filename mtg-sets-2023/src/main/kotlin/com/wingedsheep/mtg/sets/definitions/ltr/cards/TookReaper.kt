package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Took Reaper
 * {1}{W}
 * Creature — Halfling Peasant
 * 2/1
 *
 * When this creature dies, the Ring tempts you.
 */
val TookReaper = card("Took Reaper") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Halfling Peasant"
    power = 2
    toughness = 1
    oracleText = "When this creature dies, the Ring tempts you."

    triggeredAbility {
        trigger = Triggers.Dies
        effect = Effects.TheRingTemptsYou()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "35"
        artist = "Tatiana Veryayskaya"
        flavorText = "The Tooks came, a hundred strong, from Tuckborough and the Green Hills."
        imageUri = "https://cards.scryfall.io/normal/front/1/d/1da7fc15-f894-496e-ba18-a02d42e9bedc.jpg?1686967962"
    }
}
