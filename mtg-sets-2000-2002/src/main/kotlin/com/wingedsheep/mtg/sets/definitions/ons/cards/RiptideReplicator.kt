package com.wingedsheep.mtg.sets.definitions.ons.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.ChoiceType
import com.wingedsheep.sdk.scripting.EntersWithChoice
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters

/**
 * Riptide Replicator
 * {X}{4}
 * Artifact
 * As Riptide Replicator enters the battlefield, choose a color and a creature type.
 * Riptide Replicator enters the battlefield with X charge counters on it.
 * {4}, {T}: Create an X/X creature token of the chosen color and type, where X is
 * the number of charge counters on Riptide Replicator.
 */
val RiptideReplicator = card("Riptide Replicator") {
    manaCost = "{X}{4}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "As Riptide Replicator enters the battlefield, choose a color and a creature type.\nRiptide Replicator enters the battlefield with X charge counters on it.\n{4}, {T}: Create an X/X creature token of the chosen color and type, where X is the number of charge counters on Riptide Replicator."

    replacementEffect(EntersWithChoice(ChoiceType.COLOR))
    replacementEffect(EntersWithChoice(ChoiceType.CREATURE_TYPE))
    replacementEffect(EntersWithDynamicCounters(
        counterType = CounterTypeFilter.Named(Counters.CHARGE),
        count = DynamicAmount.XValue
    ))

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{4}"),
            Costs.Tap
        )
        effect = Effects.CreateTokenOfChosenColorAndType(
            dynamicPower = DynamicAmounts.countersOnSelf(CounterTypeFilter.Named(Counters.CHARGE)),
            dynamicToughness = DynamicAmounts.countersOnSelf(CounterTypeFilter.Named(Counters.CHARGE))
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "309"
        artist = "Michael Sutfin"
        flavorText = "It doesn't create just any kind of monster—it creates the best kind of monster."
        imageUri = "https://cards.scryfall.io/normal/front/4/1/41bb314f-237a-43fc-95c8-b26188dc4476.jpg?1562910457"
    }
}
