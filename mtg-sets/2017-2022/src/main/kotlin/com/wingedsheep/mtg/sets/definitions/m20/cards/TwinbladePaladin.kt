package com.wingedsheep.mtg.sets.definitions.m20.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Twinblade Paladin
 * {3}{W}
 * Creature — Human Knight
 * 3/3
 *
 * Whenever you gain life, put a +1/+1 counter on this creature.
 * As long as you have 25 or more life, this creature has double strike.
 *
 * The grow trigger uses [Triggers.YouGainLife] (fires once per life-gain event) placing a
 * single +1/+1 counter on the source. The double-strike buff is a conditional [staticAbility]
 * gated on [Conditions.LifeAtLeast] (25) granting [Keyword.DOUBLE_STRIKE] to the source; it is
 * projected through the layer system so it turns on and off as the controller's life crosses 25.
 *
 * Canonical printing: Core Set 2020 (earliest real printing). Reprinted in Foundations (FDN #503).
 */
val TwinbladePaladin = card("Twinblade Paladin") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Knight"
    power = 3
    toughness = 3
    oracleText = "Whenever you gain life, put a +1/+1 counter on this creature.\n" +
        "As long as you have 25 or more life, this creature has double strike. " +
        "(It deals both first-strike and regular combat damage.)"

    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever you gain life, put a +1/+1 counter on this creature."
    }

    staticAbility {
        condition = Conditions.LifeAtLeast(25)
        ability = GrantKeyword(Keyword.DOUBLE_STRIKE, Filters.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "285"
        artist = "Jana Schirmer"
        imageUri = "https://cards.scryfall.io/normal/front/6/3/6397d426-00e0-44da-b23c-44ccea65f5aa.jpg?1782708196"
    }
}
