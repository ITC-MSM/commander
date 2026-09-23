package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val BlossomBlessedAngel = card("Blossom-Blessed Angel") {
    manaCost = "{3}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Angel Cleric"
    power = 2
    toughness = 4
    oracleText = "Flying, vigilance\nThis creature enters prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)"

    keywords(Keyword.FLYING, Keyword.VIGILANCE)
    keywords(Keyword.PREPARED)

    prepare("Seed Suture") {
        manaCost = "{G/W}"
        typeLine = "Sorcery"
        oracleText = "Put a +1/+1 counter on target creature. You gain 1 life."
        spell {
            val creature = target("target creature", Targets.Creature)
            effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
                .then(Effects.GainLife(1))
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "3"
        artist = "Joshua Raphael"
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5e77fbf0-9d1f-4e7d-a02e-43065d11b0d9.jpg?1789692643"
        inBooster = false
    }
}
