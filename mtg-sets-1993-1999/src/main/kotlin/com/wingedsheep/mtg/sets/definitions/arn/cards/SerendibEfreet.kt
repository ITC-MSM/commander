package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Serendib Efreet
 * {2}{U}
 * Creature — Efreet
 * 3/4
 * Flying
 * At the beginning of your upkeep, this creature deals 1 damage to you.
 */
val SerendibEfreet = card("Serendib Efreet") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Efreet"
    power = 3
    toughness = 4
    oracleText = "Flying\nAt the beginning of your upkeep, this creature deals 1 damage to you."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.DealDamage(1, EffectTarget.Controller)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "20"
        artist = "Anson Maddocks"
        imageUri = "https://cards.scryfall.io/normal/front/c/f/cf56e862-3169-4f63-acd0-731080fa32f2.jpg?1562933763"
    }
}
