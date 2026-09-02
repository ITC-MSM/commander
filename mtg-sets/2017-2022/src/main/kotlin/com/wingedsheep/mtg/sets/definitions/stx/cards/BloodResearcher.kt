package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Blood Researcher — Strixhaven: School of Mages #166 (canonical printing)
 * {1}{B}{G} · Creature — Vampire Druid · 2/2
 *
 * Menace (This creature can't be blocked except by two or more creatures.)
 * Whenever you gain life, put a +1/+1 counter on this creature.
 *
 * The Ajani's Pridemate shape: [Triggers.YouGainLife] fires once per life-gain event (CR 603.2c —
 * gaining 4 life at once is one trigger), and the payoff is [Effects.AddCounters] on
 * [EffectTarget.Self].
 */
val BloodResearcher = card("Blood Researcher") {
    manaCost = "{1}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Vampire Druid"
    oracleText =
        "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "Whenever you gain life, put a +1/+1 counter on this creature."
    power = 2
    toughness = 2

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "166"
        artist = "Cristi Balanescu"
        flavorText = "\"Dean Valentin warns us not to consume the samples for our safety, but I think he's just being greedy.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/e/3e35e9ba-a10e-4926-a7a6-3a65efc2a730.jpg?1783927323"
    }
}
