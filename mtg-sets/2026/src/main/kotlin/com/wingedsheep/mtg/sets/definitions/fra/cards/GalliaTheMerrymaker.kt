package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

val GalliaTheMerrymaker = card("Gallia, the Merrymaker") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Satyr"
    power = 2
    toughness = 1
    oracleText = "Haste\n" +
        "Each other creature you control with +1/+1 counter on it has haste.\n" +
        "{1}{R}, {T}: Put a +1/+1 counter on target creature that entered this turn."

    keywords(Keyword.HASTE)

    staticAbility {
        ability = GrantKeyword(
            Keyword.HASTE,
            GroupFilter(GameObjectFilter.Creature.youControl().withCounter(Counters.PLUS_ONE_PLUS_ONE)).other()
        )
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{R}"), Costs.Tap)
        val t = target(
            "target creature that entered this turn",
            TargetCreature(filter = TargetFilter(GameObjectFilter.Creature.enteredThisTurn()))
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, t)
        description = "{1}{R}, {T}: Put a +1/+1 counter on target creature that entered this turn."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "245"
        artist = "Andrea Piparo"
        flavorText = "All of Theros is her dance floor."
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f27d50f0-d76e-4ce1-a8d9-d997af6a5b41.jpg?1789385716"
        inBooster = false
    }
}
