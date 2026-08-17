package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sprite Dragon
 * {U}{R}
 * Creature — Faerie Dragon
 * 1/1
 * Flying, haste
 * Whenever you cast a noncreature spell, put a +1/+1 counter on this creature.
 *
 * A prowess-shaped payoff that keeps what it earns: [Triggers.YouCastNoncreature] is the
 * `Player.You` + noncreature-filtered cast watcher, and the growth is a permanent
 * [Effects.AddCounters] on itself rather than an until-end-of-turn pump.
 */
val SpriteDragon = card("Sprite Dragon") {
    manaCost = "{U}{R}"
    colorIdentity = "RU"
    typeLine = "Creature — Faerie Dragon"
    power = 1
    toughness = 1
    oracleText = "Flying, haste\n" +
        "Whenever you cast a noncreature spell, put a +1/+1 counter on this creature."

    keywords(Keyword.FLYING, Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever you cast a noncreature spell, put a +1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "211"
        artist = "Gabor Szikszai"
        flavorText = "Size of a pixie, rage of a hellkite."
        imageUri = "https://cards.scryfall.io/normal/front/2/8/281f6118-adb8-4a7d-9c77-5570f3399e6e.jpg?1783931016"
    }
}
