package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Heirs of Stromkirk
 * {2}{R}{R}
 * Creature — Vampire
 * 2 / 2
 *
 * Intimidate (This creature can't be blocked except by artifact creatures and/or creatures that
 * share a color with it.)
 * Whenever this creature deals combat damage to a player, put a +1/+1 counter on it.
 *
 * [Triggers.DealsCombatDamageToPlayer] is the SELF-bound combat-damage-to-a-player trigger; "it"
 * is the Heirs themselves, so the counter lands on [EffectTarget.Self].
 */
val HeirsOfStromkirk = card("Heirs of Stromkirk") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Vampire"
    power = 2
    toughness = 2
    oracleText = "Intimidate (This creature can't be blocked except by artifact creatures and/or creatures that " +
        "share a color with it.)\n" +
        "Whenever this creature deals combat damage to a player, put a +1/+1 counter on it."

    keywords(Keyword.INTIMIDATE)

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "140"
        artist = "Winona Nelson"
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ff89ad3b-b154-49e2-a0fd-135279512250.jpg?1783940684"
    }
}
