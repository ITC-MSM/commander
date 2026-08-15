package com.wingedsheep.mtg.sets.definitions.gpt.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DividedDamageEffect
import com.wingedsheep.sdk.scripting.targets.AnyTarget

/**
 * Electrolyze
 * {1}{U}{R}
 * Instant
 * Electrolyze deals 2 damage divided as you choose among one or two targets.
 * Draw a card.
 */
val Electrolyze = card("Electrolyze") {
    manaCost = "{1}{U}{R}"
    colorIdentity = "UR"
    typeLine = "Instant"
    oracleText = "Electrolyze deals 2 damage divided as you choose among one or two targets.\nDraw a card."

    spell {
        target = AnyTarget(count = 2, minCount = 1)
        effect = Effects.Composite(
            DividedDamageEffect(
                totalDamage = 2,
                minTargets = 1,
                maxTargets = 2
            ),
            Effects.DrawCards(1)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "111"
        artist = "Zoltan Boros & Gabor Szikszai"
        imageUri = "https://cards.scryfall.io/normal/front/e/f/ef42b5b2-6504-486c-aaa0-9d5e4769ba1d.jpg?1593272648"
    }
}
