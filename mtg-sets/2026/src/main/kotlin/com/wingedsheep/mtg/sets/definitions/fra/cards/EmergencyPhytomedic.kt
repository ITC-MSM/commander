package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val EmergencyPhytomedic = card("Emergency Phytomedic") {
    manaCost = "{G/W}"
    colorIdentity = "GW"
    typeLine = "Creature — Dryad Cleric"
    power = 1
    toughness = 1
    oracleText = "This creature enters prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)"

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
        collectorNumber = "130"
        artist = "Dmitry Burmak"
        imageUri = "https://cards.scryfall.io/normal/front/d/e/de94d388-919d-44ff-baef-8c90a417ac6d.jpg?1789637792"
        inBooster = false
    }
}
