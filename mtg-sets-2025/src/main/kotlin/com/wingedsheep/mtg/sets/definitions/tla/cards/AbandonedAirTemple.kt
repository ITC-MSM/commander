package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Abandoned Air Temple
 * Land
 * This land enters tapped unless you control a basic land.
 * {T}: Add {W}.
 * {3}{W}, {T}: Put a +1/+1 counter on each creature you control.
 */
val AbandonedAirTemple = card("Abandoned Air Temple") {
    typeLine = "Land"
    colorIdentity = "W"
    oracleText = "This land enters tapped unless you control a basic land.\n" +
            "{T}: Add {W}.\n" +
            "{3}{W}, {T}: Put a +1/+1 counter on each creature you control."

    replacementEffect(
        EntersTapped(
            unlessCondition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.BasicLand)
        )
    )

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{W}"), Costs.Tap)
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.youControl()),
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        )
        description = "{3}{W}, {T}: Put a +1/+1 counter on each creature you control."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "263"
        artist = "Dom Lay"
        flavorText = "Aang once knew this place to be full of monks, lemurs, and bison."
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c0433f9-8f1e-4a19-a83f-a41925f1b1a9.jpg?1764121937"
    }
}
