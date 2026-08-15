package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Cactuar
 * {G}
 * Creature — Plant
 * 3/3
 *
 * Trample
 * At the beginning of your end step, if this creature didn't enter the battlefield this turn,
 * return it to its owner's hand.
 *
 * Intervening-if (CR 603.4): the bounce trigger only fires if Cactuar didn't enter this turn —
 * modeled as the negation of [Conditions.SourceEnteredThisTurn]. The condition is re-checked at
 * resolution, so a Cactuar that entered this turn is left alone (it can attack and stay for the
 * turn it arrives, then bounces on a later end step).
 */
val Cactuar = card("Cactuar") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant"
    power = 3
    toughness = 3
    oracleText = "Trample\nAt the beginning of your end step, if this creature didn't enter the " +
        "battlefield this turn, return it to its owner's hand."

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.Not(Conditions.SourceEnteredThisTurn)
        effect = Effects.ReturnToHand(EffectTarget.Self)
        description = "At the beginning of your end step, if this creature didn't enter the " +
            "battlefield this turn, return it to its owner's hand."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "177"
        artist = "Kevin Sidharta"
        flavorText = "A cactus with legs that loves to run hither and thither. Watch out for its thorns!"
        imageUri = "https://cards.scryfall.io/normal/front/d/5/d5273db4-6214-41e4-825a-612fca8bbe03.jpg?1748706421"
    }
}
