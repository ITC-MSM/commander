package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Demon's Due
 * {3}{B}
 * Instant
 * Scry 2, then draw two cards. You lose 2 life.
 */
val DemonsDue = card("Demon's Due") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Scry 2, then draw two cards. You lose 2 life."

    spell {
        effect = Effects.Scry(2)
            .then(Effects.DrawCards(2))
            .then(Effects.LoseLife(2, EffectTarget.Controller))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "75"
        artist = "Slawomir Maniak"
        flavorText = "Ob Nixilis extends his hand not in partnership, but to affirm your subservience."
        imageUri = "https://cards.scryfall.io/normal/front/2/e/2e59fb98-c887-42f1-a620-9e6b40b94cb5.jpg?1783923132"
    }
}
