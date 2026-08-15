package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.dsl.Conditions

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Arrow Storm
 * {3}{R}{R}
 * Sorcery
 * Arrow Storm deals 4 damage to any target.
 * Raid — If you attacked this turn, instead Arrow Storm deals 5 damage to that
 * permanent or player and the damage can't be prevented.
 */
val ArrowStorm = card("Arrow Storm") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Arrow Storm deals 4 damage to any target.\nRaid — If you attacked this turn, instead Arrow Storm deals 5 damage to that permanent or player and the damage can't be prevented."

    spell {
        val t = target("any target", Targets.Any)
        effect = ConditionalEffect(
            condition = Conditions.YouAttackedThisTurn,
            effect = DealDamageEffect(5, t, cantBePrevented = true),
            elseEffect = DealDamageEffect(4, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "98"
        artist = "Steve Prescott"
        flavorText = "\"First the thunder, then the rain.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/5/c57534fb-2591-4003-aeec-6452faa4a759.jpg?1562793262"
    }
}
