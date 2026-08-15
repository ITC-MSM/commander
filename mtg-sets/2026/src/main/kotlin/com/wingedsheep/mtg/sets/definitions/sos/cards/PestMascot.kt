package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Pest Mascot — Secrets of Strixhaven #209
 * {1}{B}{G} · Creature — Pest Ape · 2/3
 *
 * Trample
 * Whenever you gain life, put a +1/+1 counter on this creature.
 *
 * Like Essence Channeler, the trigger fires once per life-gaining event regardless of how much
 * life was gained.
 */
val PestMascot = card("Pest Mascot") {
    manaCost = "{1}{B}{G}"
    colorIdentity = "BG"
    typeLine = "Creature — Pest Ape"
    power = 2
    toughness = 3
    oracleText = "Trample\nWhenever you gain life, put a +1/+1 counter on this creature."

    keywords(Keyword.TRAMPLE)

    // Whenever you gain life, put a +1/+1 counter on this creature.
    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "209"
        artist = "Filipe Pagliuso"
        flavorText = "Hungering for the zest of life."
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d882beb9-6766-4818-afbb-f6fd7a2d5b70.jpg?1775938452"
    }
}
