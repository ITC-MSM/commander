package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.enduring
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.ContextPropertyKey
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Enduring Tenacity
 * {2}{B}{B}
 * Enchantment Creature — Snake Glimmer
 * 4/3
 * Whenever you gain life, target opponent loses that much life.
 * When Enduring Tenacity dies, if it was a creature, return it to the battlefield under its
 *   owner's control. It's an enchantment. (It's not a creature.)
 *
 * "That much" is the amount of life gained by the triggering life-gain event, read from the
 * trigger context as [ContextPropertyKey.TRIGGER_LIFE_GAINED]. The drain targets a single
 * opponent (CR: "target opponent"). The death clause is the Duskmourn "Enduring" mechanic —
 * see [enduring].
 */
val EnduringTenacity = card("Enduring Tenacity") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Enchantment Creature — Snake Glimmer"
    oracleText = "Whenever you gain life, target opponent loses that much life.\n" +
        "When Enduring Tenacity dies, if it was a creature, return it to the battlefield under " +
        "its owner's control. It's an enchantment. (It's not a creature.)"
    power = 4
    toughness = 3

    enduring()

    triggeredAbility {
        trigger = Triggers.YouGainLife
        target = Targets.Opponent
        effect = Effects.LoseLife(
            DynamicAmount.ContextProperty(ContextPropertyKey.TRIGGER_LIFE_GAINED),
            EffectTarget.ContextTarget(0)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "95"
        artist = "Isis"
        imageUri = "https://cards.scryfall.io/normal/front/d/5/d5756d4b-3068-412c-8643-880d3459151e.jpg?1726286203"
    }
}
