package com.wingedsheep.mtg.sets.definitions.csp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ronom Unicorn
 * {1}{W}
 * Creature — Unicorn
 * 2/2
 *
 * Sacrifice this creature: Destroy target enchantment.
 */
val RonomUnicorn = card("Ronom Unicorn") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Unicorn"
    oracleText = "Sacrifice this creature: Destroy target enchantment."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.SacrificeSelf
        target = Targets.Enchantment
        effect = Effects.Destroy(EffectTarget.ContextTarget(0))
        description = "Sacrifice this creature: Destroy target enchantment."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "16"
        artist = "Carl Critchlow"
        flavorText = "The aberrant magic of the Rimewind drew the unicorns back from the northern wastes to do battle once again."
        imageUri = "https://cards.scryfall.io/normal/front/a/e/ae4764e1-e47c-4934-86a5-9b432c29a158.jpg?1783943367"
    }
}
