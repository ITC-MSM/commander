package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ancient Craving
 * {3}{B}
 * Sorcery
 * You draw three cards and you lose 3 life.
 *
 * Portal Second Age is the card's earliest real-expansion printing, so the canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives here.
 *
 * The life loss is not a cost and not optional — both halves resolve, in printed order, and the
 * draw happens even if the loss would be lethal (state-based actions check afterwards).
 */
val AncientCraving = card("Ancient Craving") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "You draw three cards and you lose 3 life."

    spell {
        effect = Effects.Composite(
            Effects.DrawCards(3),
            Effects.LoseLife(3, EffectTarget.Controller),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "62"
        artist = "Rob Alexander"
        flavorText = "\"Knowledge demands sacrifice.\"\n—Tojira, swamp queen"
        imageUri = "https://cards.scryfall.io/normal/front/3/7/37ee59a7-0f41-4d5d-b049-71ca1ba335b1.jpg?1783946481"
    }
}
