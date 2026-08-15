package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Island of Wak-Wak
 * Land
 * {T}: Target creature with flying has base power 0 until end of turn.
 */
val IslandOfWakWak = card("Island of Wak-Wak") {
    typeLine = "Land"
    colorIdentity = ""
    oracleText = "{T}: Target creature with flying has base power 0 until end of turn."

    activatedAbility {
        cost = Costs.Tap
        val creature = target(
            "target creature with flying",
            TargetObject(filter = TargetFilter(GameObjectFilter.Creature.withKeyword(Keyword.FLYING)))
        )
        effect = Effects.SetBasePower(creature, DynamicAmount.Fixed(0), Duration.EndOfTurn)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "75"
        artist = "Douglas Shuler"
        flavorText = "The Isle of Wak-Wak, home to a tribe of winged folk, is named for a peculiar fruit that grows there."
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f09cbd18-79f1-49a0-a3bd-b380ff5ecf03.jpg?1562940263"
    }
}
