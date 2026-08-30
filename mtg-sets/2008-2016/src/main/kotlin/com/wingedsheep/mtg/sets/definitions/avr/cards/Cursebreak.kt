package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Cursebreak
 * {1}{W}
 * Instant
 *
 * Destroy target enchantment. You gain 2 life.
 *
 * Two printed sentences, so two members of one flat [Effects.Composite]: the destroy (which is a
 * move to the graveyard `byDestruction`, so indestructible and regeneration are honoured) and the
 * life gain, whose default target is already the controller.
 */
val Cursebreak = card("Cursebreak") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Destroy target enchantment. You gain 2 life."

    spell {
        val enchantment = target("target", Targets.Enchantment)
        effect = Effects.Composite(
            Effects.Destroy(enchantment),
            Effects.GainLife(2),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "14"
        artist = "Sam Wolfe Connelly"
        flavorText = "No longer the village pariah. No longer taunted and shamed. Sigrun was finally free."
        imageUri = "https://cards.scryfall.io/normal/front/c/7/c71a0883-316c-4870-a029-25f16952fbc0.jpg?1783940738"
    }
}
