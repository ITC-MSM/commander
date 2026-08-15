package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rampant Elephant
 * {3}{W}
 * Creature — Elephant
 * 2/2
 *
 * {G}: Target creature blocks this creature this turn if able.
 *
 * The forced-block ability uses [Effects.ForceBlock] (no untap, unlike Provoke).
 */
val RampantElephant = card("Rampant Elephant") {
    manaCost = "{3}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Elephant"
    power = 2
    toughness = 2
    oracleText = "{G}: Target creature blocks this creature this turn if able."

    activatedAbility {
        cost = Costs.Mana("{G}")
        target = Targets.Creature
        effect = Effects.ForceBlock(EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "28"
        artist = "Alan Pollack"
        flavorText = "No matter how righteous the cause, it helps to bring along some muscle."
        imageUri = "https://cards.scryfall.io/normal/front/7/5/752642d2-3dad-4f58-b154-beb5982141dc.jpg?1562918488"
    }
}
