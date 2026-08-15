package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Slumbering Cerberus
 * {1}{R}
 * Creature — Dog
 * 4/2
 * This creature doesn't untap during your untap step.
 * Morbid — At the beginning of each end step, if a creature died this turn, untap this creature.
 */
val SlumberingCerberus = card("Slumbering Cerberus") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dog"
    power = 4
    toughness = 2
    oracleText = "This creature doesn't untap during your untap step.\n" +
        "Morbid — At the beginning of each end step, if a creature died this turn, untap this creature."

    // "This creature doesn't untap during your untap step."
    flags(AbilityFlag.DOESNT_UNTAP)

    // "Morbid — At the beginning of each end step, if a creature died this turn, untap this creature."
    // The intervening-if (Rule 603.4) is checked when the trigger would fire AND again on resolution.
    triggeredAbility {
        trigger = Triggers.EachEndStep
        triggerCondition = Conditions.CreatureDiedThisTurn
        effect = Effects.Untap(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "94"
        artist = "Kari Christensen"
        flavorText = "The plan seemed to be going perfectly, until Mino realized the third head was no longer snoring."
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9d06faa8-201d-45db-b398-ad56f7b01848.jpg?1782689185"
    }
}
