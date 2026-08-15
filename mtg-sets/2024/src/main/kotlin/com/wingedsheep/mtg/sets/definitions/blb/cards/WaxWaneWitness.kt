package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Wax-Wane Witness
 * {3}{W}
 * Creature — Bat Cleric
 * 2/4
 *
 * Flying, vigilance
 * Whenever you gain or lose life during your turn, this creature gets +1/+0 until end of turn.
 */
val WaxWaneWitness = card("Wax-Wane Witness") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bat Cleric"
    power = 2
    toughness = 4
    oracleText = "Flying, vigilance\nWhenever you gain or lose life during your turn, this creature gets +1/+0 until end of turn."

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.YouGainLife
        triggerCondition = Conditions.IsYourTurn
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    triggeredAbility {
        trigger = Triggers.YouLoseLife
        triggerCondition = Conditions.IsYourTurn
        effect = Effects.ModifyStats(1, 0, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "39"
        artist = "Mila Pesic"
        flavorText = "Batfolk clerics consider the cycle of light and darkness as a sacred allegory of life and death."
        imageUri = "https://cards.scryfall.io/normal/front/d/9/d90ea719-5320-46c6-a347-161853a14776.jpg?1721426005"
    }
}
