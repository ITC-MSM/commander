package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

val VigorbloomVanguard = card("Vigorbloom Vanguard") {
    manaCost = "{1}{G/W}"
    colorIdentity = "GW"
    typeLine = "Creature — Troll Druid"
    power = 2
    toughness = 2
    oracleText = "This creature enters prepared. (While it's prepared, you may cast a copy of its spell. Doing so unprepares it.)\nEach creature you control with a +1/+1 counter on it has vigilance."

    keywords(Keyword.PREPARED)

    staticAbility {
        ability = GrantKeyword(
            Keyword.VIGILANCE,
            GroupFilter(GameObjectFilter.Creature.youControl().withCounter(Counters.PLUS_ONE_PLUS_ONE))
        )
    }

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
        rarity = Rarity.UNCOMMON
        collectorNumber = "161"
        artist = "Denman Rooke"
        imageUri = "https://cards.scryfall.io/normal/front/a/c/acefc515-bf97-4dc0-b0f7-ae8ae5a61671.jpg?1788329423"
        inBooster = false
    }
}
