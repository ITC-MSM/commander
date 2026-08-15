package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Repulse
 * {2}{U}
 * Instant
 * Return target creature to its owner's hand.
 * Draw a card.
 */
val Repulse = card("Repulse") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Return target creature to its owner's hand.\nDraw a card."

    spell {
        target = TargetCreature()
        effect = Effects.ReturnToHand(EffectTarget.ContextTarget(0))
            .then(Effects.DrawCards(1))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "70"
        artist = "Aaron Boyd"
        flavorText = "\"You aren't invited.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9a04e9be-48be-440e-9825-cfffd4c2b1a4.jpg?1562926068"
    }
}
