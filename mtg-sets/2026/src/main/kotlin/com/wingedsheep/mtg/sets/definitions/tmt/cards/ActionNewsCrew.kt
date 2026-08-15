package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.AddCountersEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Action News Crew
 * {1}{W}
 * Creature — Human Citizen
 * 2/2
 *
 * Vigilance
 * Channel — {6}, Discard this card: Put a +1/+1 counter on each
 * creature you control. Draw a card.
 */
val ActionNewsCrew = card("Action News Crew") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Citizen"
    oracleText = "Vigilance\nChannel — {6}, Discard this card: Put a +1/+1 counter on each creature you control. Draw a card."
    power = 2
    toughness = 2

    keywords(Keyword.VIGILANCE)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{6}"), Costs.DiscardSelf)
        activateFromZone = Zone.HAND
        effect = Effects.ForEachInGroup(
            filter = GroupFilter(GameObjectFilter.Creature.youControl()),
            effect = AddCountersEffect(
                counterType = Counters.PLUS_ONE_PLUS_ONE,
                count = 1,
                target = EffectTarget.Self
            )
        ).then(Effects.DrawCards(1))
        description = "Channel — {6}, Discard this card: Put a +1/+1 counter on each creature you control. Draw a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "1"
        artist = "Gabriel Tanko"
        flavorText = "\"I just hope this isn't another wild turtle chase!\"\n—Vernon Fenwick"
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bc0f5ca8-47bd-4451-8fd1-a312ff7d31ec.jpg?1771342158"
    }
}
