package com.wingedsheep.mtg.sets.definitions.jmp.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Chained Brute
 * {1}{R}
 * Creature — Devil
 * 4/3
 *
 * This creature doesn't untap during your untap step.
 * {1}, Sacrifice another creature: Untap this creature. Activate only during your turn.
 */
val ChainedBrute = card("Chained Brute") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Devil"
    oracleText = "This creature doesn't untap during your untap step.\n{1}, Sacrifice another creature: Untap this creature. Activate only during your turn."
    power = 4
    toughness = 3

    flags(AbilityFlag.DOESNT_UNTAP)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeAnother(GameObjectFilter.Creature))
        effect = Effects.Untap(EffectTarget.Self)
        restrictions = listOf(ActivationRestriction.OnlyDuringYourTurn)
        description = "{1}, Sacrifice another creature: Untap this creature. Activate only during your turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "19"
        artist = "Dan Murayama Scott"
        flavorText = "A momentary lapse is all it needs to break free."
        imageUri = "https://cards.scryfall.io/normal/front/1/d/1d4e5c23-3a7f-4a6b-99c4-6a1487a9b097.jpg?1783930504"
    }
}
