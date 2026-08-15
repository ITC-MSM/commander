package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.GrantSubtype
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Skyknight Squire
 * {1}{W}
 * Creature — Cat Scout
 * 1/1
 *
 * Whenever another creature you control enters, put a +1/+1 counter on this creature.
 * As long as this creature has three or more +1/+1 counters on it, it has flying and is
 * a Knight in addition to its other types.
 *
 * The grow trigger uses [Triggers.OtherCreatureEnters] (OTHER binding, Creature.youControl()
 * filter). The threshold buff is modeled as two conditional [staticAbility] blocks gated on
 * [Conditions.SourceCounterCountAtLeast] — one granting FLYING, one granting the Knight
 * subtype ([GrantSubtype] is additive, "in addition to its other types"). Both apply only
 * while the source carries 3+ +1/+1 counters, projected through the layer system.
 */
val SkyknightSquire = card("Skyknight Squire") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Cat Scout"
    power = 1
    toughness = 1
    oracleText = "Whenever another creature you control enters, put a +1/+1 counter on this creature.\n" +
        "As long as this creature has three or more +1/+1 counters on it, it has flying and is a " +
        "Knight in addition to its other types."

    triggeredAbility {
        trigger = Triggers.OtherCreatureEnters
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever another creature you control enters, put a +1/+1 counter on this creature."
    }

    staticAbility {
        condition = Conditions.SourceCounterCountAtLeast(Counters.PLUS_ONE_PLUS_ONE, 3)
        ability = GrantKeyword(Keyword.FLYING, Filters.Self)
    }
    staticAbility {
        condition = Conditions.SourceCounterCountAtLeast(Counters.PLUS_ONE_PLUS_ONE, 3)
        ability = GrantSubtype("Knight", Filters.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "23"
        artist = "Alexander Mokhov"
        flavorText = "Before Pashin could become the greatest skyknight in the kingdom, he'd have to survive his first flight."
        imageUri = "https://cards.scryfall.io/normal/front/f/c/fcfe4e62-c153-47b8-8e09-cedaf91f53d8.jpg?1782689245"
    }
}
