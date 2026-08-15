package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.AnyTarget

/**
 * Zap
 * {2}{R}
 * Instant
 * Zap deals 1 damage to any target.
 * Draw a card.
 */
val Zap = card("Zap") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "Zap deals 1 damage to any target.\nDraw a card."

    spell {
        target = AnyTarget()
        effect = DealDamageEffect(1, EffectTarget.ContextTarget(0)) then Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "180"
        artist = "John Matson"
        flavorText = "\"All this time I thought Squee was useless,\" chuckled Sisay. \"Who knew he'd be such a good shot?\""
        imageUri = "https://cards.scryfall.io/normal/front/7/5/7502ce01-b762-40fe-a064-c7b20b08a722.jpg?1562918451"
    }
}
