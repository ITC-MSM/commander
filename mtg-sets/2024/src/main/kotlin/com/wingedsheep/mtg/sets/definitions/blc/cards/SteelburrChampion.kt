package com.wingedsheep.mtg.sets.definitions.blc.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Steelburr Champion
 * {2}{W}
 * Creature — Mouse Soldier
 * 1/1
 *
 * Offspring {1}{W}
 * Vigilance
 * Whenever an opponent casts a noncreature spell, put a +1/+1 counter on this creature.
 */
val SteelburrChampion = card("Steelburr Champion") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Mouse Soldier"
    oracleText = "Offspring {1}{W} (You may pay an additional {1}{W} as you cast this spell. " +
        "If you do, when this creature enters, create a 1/1 token copy of it.)\n" +
        "Vigilance\n" +
        "Whenever an opponent casts a noncreature spell, put a +1/+1 counter on this creature."
    power = 1
    toughness = 1

    keywords(Keyword.VIGILANCE)

    // Offspring: modeled as kicker-like additional cost
    keywordAbility(KeywordAbility.offspring("{1}{W}"))

    // Offspring ETB: when this enters, if offspring was paid, create a 1/1 token copy
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        interveningIf = Conditions.WasKicked
        effect = Effects.CreateTokenCopyOfSelf(overridePower = 1, overrideToughness = 1)
    }

    triggeredAbility {
        trigger = Triggers.opponentCasts(GameObjectFilter.Noncreature)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "12"
        artist = "Josiah \"Jo\" Cameron"
        flavorText = "\"The secret to victory is simple. Don't lose.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/3/73b1f962-22b7-4394-9796-24f7b0f8a42f.jpg?1783910734"
    }
}
