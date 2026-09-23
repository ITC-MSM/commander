package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Teyo, Lightshield Expert — the creature and planeswalker riders are two independent resolution-time type
 * checks on the same target (read off projected state via [Conditions.TargetMatchesFilter]), so a
 * planeswalker creature gets both counters and a noncreature, nonplaneswalker permanent gets neither.
 */
val TeyoLightshieldExpert = card("Teyo, Lightshield Expert") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Human Cleric"
    oracleText = "Flash\nWhen Teyo enters, target permanent you control gains hexproof until end of turn. " +
        "Put a +1/+1 counter on it if it's a creature. Put a loyalty counter on it if it's a planeswalker. " +
        "(It can't be the target of spells or abilities your opponents control.)"
    power = 1
    toughness = 1

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val permanent = target("permanent you control", Targets.PermanentYouControl)
        effect = Effects.Composite(
            Effects.GrantKeyword(Keyword.HEXPROOF, permanent),
            ConditionalEffect(
                condition = Conditions.TargetMatchesFilter(GameObjectFilter.Creature),
                effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, permanent),
            ),
            ConditionalEffect(
                condition = Conditions.TargetMatchesFilter(GameObjectFilter.Planeswalker),
                effect = Effects.AddCounters(Counters.LOYALTY, 1, permanent),
            ),
        )
        description = "When Teyo enters, target permanent you control gains hexproof until end of turn. " +
            "Put a +1/+1 counter on it if it's a creature. Put a loyalty counter on it if it's a planeswalker."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "204"
        artist = "Anna Steinbauer"
        imageUri = "https://cards.scryfall.io/normal/front/5/f/5f7521d7-9f1f-4f03-b2ea-dd2a1b1e4e5b.jpg?1789471515"
        inBooster = false
    }
}
