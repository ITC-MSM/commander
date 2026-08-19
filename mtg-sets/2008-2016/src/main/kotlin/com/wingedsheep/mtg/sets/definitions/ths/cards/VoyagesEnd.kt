package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Voyage's End
 * {1}{U}
 * Instant
 *
 * Return target creature to its owner's hand. Scry 1. (Look at the top card of your library. You may put that card on the bottom.)
 */
val VoyagesEnd = card("Voyage's End") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Return target creature to its owner's hand. Scry 1. (Look at the top card of your library. You may put that card on the bottom.)"

    spell {
        target = Targets.Creature
        effect = Effects.ReturnToHand(EffectTarget.ContextTarget(0))
            .then(Effects.Scry(1))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "73"
        artist = "Chris Rahn"
        flavorText = "Philosophers say those lost at sea ascended to a more perfect realm. Sailors say they drowned."
        imageUri = "https://cards.scryfall.io/normal/front/3/9/3992ae67-ad0b-40be-a97d-d7fb36754918.jpg?1783939787"
    }
}
