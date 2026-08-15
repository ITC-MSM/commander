package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.FlipCoinEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mijae Djinn
 * {R}{R}{R}
 * Creature — Djinn
 * 6/3
 * Whenever this creature attacks, flip a coin. If you lose the flip, remove this creature
 * from combat and tap it.
 */
val MijaeDjinn = card("Mijae Djinn") {
    manaCost = "{R}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Djinn"
    power = 6
    toughness = 3
    oracleText = "Whenever this creature attacks, flip a coin. If you lose the flip, remove this creature from combat and tap it."

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = FlipCoinEffect(
            lostEffect = Effects.RemoveFromCombat(EffectTarget.Self).then(Effects.Tap(EffectTarget.Self)),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "42"
        artist = "Susan Van Camp"
        imageUri = "https://cards.scryfall.io/normal/front/d/3/d3ddbe51-cd1a-4b2c-849a-7c82d622122a.jpg?1562934600"
    }
}
