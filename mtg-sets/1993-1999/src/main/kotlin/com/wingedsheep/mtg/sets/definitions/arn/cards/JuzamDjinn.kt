package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Juzám Djinn
 * {2}{B}{B}
 * Creature — Djinn
 * 5/5
 * At the beginning of your upkeep, this creature deals 1 damage to you.
 */
val JuzamDjinn = card("Juzám Djinn") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Djinn"
    power = 5
    toughness = 5
    oracleText = "At the beginning of your upkeep, this creature deals 1 damage to you."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.DealDamage(1, EffectTarget.Controller)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "29"
        artist = "Mark Tedin"
        flavorText = "\"Expect my visit when the darkness comes. The night I think is best for hiding all.\" —Ouallada"
        imageUri = "https://cards.scryfall.io/normal/front/3/1/31bf3f14-b5df-498b-a1bb-965885c82401.jpg?1562904228"
    }
}
