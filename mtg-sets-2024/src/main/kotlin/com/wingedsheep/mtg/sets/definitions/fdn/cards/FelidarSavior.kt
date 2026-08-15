package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Felidar Savior
 * {3}{W}
 * Creature — Cat Beast
 * 2/3
 *
 * Lifelink
 * When this creature enters, put a +1/+1 counter on each of up to two other
 * target creatures you control.
 *
 * The ETB uses an optional `count = 2` [TargetCreature] (0–2 targets) filtered to
 * [TargetFilter.OtherCreatureYouControl] (excludes itself, restricted to creatures
 * you control), fanned out with [ForEachTargetEffect] so each chosen creature
 * receives one +1/+1 counter.
 */
val FelidarSavior = card("Felidar Savior") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat Beast"
    power = 2
    toughness = 3
    oracleText = "Lifelink (Damage dealt by this creature also causes you to gain that much life.)\n" +
        "When this creature enters, put a +1/+1 counter on each of up to two other target creatures you control."

    keywords(Keyword.LIFELINK)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        target(
            "up to two other target creatures you control",
            TargetCreature(count = 2, optional = true, filter = TargetFilter.OtherCreatureYouControl),
        )
        effect = ForEachTargetEffect(
            listOf(Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.ContextTarget(0))),
        )
        description = "When this creature enters, put a +1/+1 counter on each of up to two other target creatures you control."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "12"
        artist = "Ilse Gort"
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cd092b14-d72f-4de0-8f19-1338661b9e3b.jpg?1782689256"
    }
}
