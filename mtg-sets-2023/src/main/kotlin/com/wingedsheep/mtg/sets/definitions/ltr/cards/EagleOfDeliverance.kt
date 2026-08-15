package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Eagle of Deliverance
 * {4}{W}{W}
 * Creature — Bird Soldier
 * 5/5
 * Flying
 * When this creature enters, put an indestructible counter on another target creature you control.
 * Draw a card if that creature's power is 2 or less.
 */
val EagleOfDeliverance = card("Eagle of Deliverance") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird Soldier"
    power = 5
    toughness = 5
    oracleText = "Flying\n" +
        "When this creature enters, put an indestructible counter on another target creature you control. Draw a card if that creature's power is 2 or less."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target(
            "another target creature you control",
            TargetCreature(filter = TargetFilter.OtherCreatureYouControl)
        )
        effect = Effects.AddCounters(Counters.INDESTRUCTIBLE, 1, creature) then
            ConditionalEffect(
                condition = Conditions.TargetPowerAtMost(DynamicAmount.Fixed(2)),
                effect = Effects.DrawCards(1)
            )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "824"
        artist = "Sidharth Chaturvedi"
        flavorText = "Even as he came swooping down, Gwaihir saw the two figures fall, worn out, hiding their eyes from death."
        imageUri = "https://cards.scryfall.io/normal/front/a/4/a4631187-9a5b-43d5-92d8-de6e609d2a03.jpg?1719684215"
    }
}
