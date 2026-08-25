package com.wingedsheep.mtg.sets.definitions.som.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Golem's Heart
 * {2}
 * Artifact
 *
 * Whenever a player casts an artifact spell, you may gain 1 life.
 */
val GolemsHeart = card("Golem's Heart") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Whenever a player casts an artifact spell, you may gain 1 life."

    triggeredAbility {
        trigger = Triggers.anyPlayerCasts(GameObjectFilter.Artifact)
        optional = true
        effect = Effects.GainLife(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "161"
        artist = "Matt Cavotta"
        flavorText = "The heart of a golem gives life to more than just the iron husk around it."
        imageUri = "https://cards.scryfall.io/normal/front/6/4/647ecb81-2d23-40f3-8570-0b86e2ed1c5e.jpg?1783941707"
    }
}
