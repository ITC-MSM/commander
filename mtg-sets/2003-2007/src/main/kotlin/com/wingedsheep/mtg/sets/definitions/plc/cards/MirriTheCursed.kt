package com.wingedsheep.mtg.sets.definitions.plc.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Mirri the Cursed
 * {2}{B}{B}
 * Legendary Creature — Vampire Cat
 * 3/2
 * Flying, first strike, haste
 * Whenever Mirri deals combat damage to a creature, put a +1/+1 counter on Mirri.
 */
val MirriTheCursed = card("Mirri the Cursed") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Vampire Cat"
    power = 3
    toughness = 2
    oracleText = "Flying, first strike, haste\n" +
        "Whenever Mirri deals combat damage to a creature, put a +1/+1 counter on Mirri."

    keywords(Keyword.FLYING, Keyword.FIRST_STRIKE, Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToCreature
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "75"
        artist = "Kev Walker"
        flavorText = "A hero fails, a martyr falls. Time twists and destinies interchange."
        imageUri = "https://cards.scryfall.io/normal/front/6/d/6da09233-952f-4784-995e-0d85d8b56637.jpg"
    }
}
