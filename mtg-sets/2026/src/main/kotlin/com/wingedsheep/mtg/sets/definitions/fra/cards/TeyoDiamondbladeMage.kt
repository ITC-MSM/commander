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
 * Teyo, Diamondblade Mage — the creature and planeswalker riders are two independent resolution-time type
 * checks on the same target (read off projected state via [Conditions.TargetMatchesFilter]), so a
 * planeswalker creature gets both counters and a noncreature, nonplaneswalker permanent gets neither.
 */
val TeyoDiamondbladeMage = card("Teyo, Diamondblade Mage") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Human Warlock"
    oracleText = "Flash\nWhen Teyo enters, target permanent you control gains deathtouch until end of turn. " +
        "Put a +1/+1 counter on it if it's a creature. Put a loyalty counter on it if it's a planeswalker."
    power = 3
    toughness = 1

    keywords(Keyword.FLASH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val permanent = target("permanent you control", Targets.PermanentYouControl)
        effect = Effects.Composite(
            Effects.GrantKeyword(Keyword.DEATHTOUCH, permanent),
            ConditionalEffect(
                condition = Conditions.TargetMatchesFilter(GameObjectFilter.Creature),
                effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, permanent),
            ),
            ConditionalEffect(
                condition = Conditions.TargetMatchesFilter(GameObjectFilter.Planeswalker),
                effect = Effects.AddCounters(Counters.LOYALTY, 1, permanent),
            ),
        )
        description = "When Teyo enters, target permanent you control gains deathtouch until end of turn. " +
            "Put a +1/+1 counter on it if it's a creature. Put a loyalty counter on it if it's a planeswalker."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "236"
        artist = "Anna Steinbauer"
        flavorText = "\"I once witnessed the power of a diamondstorm. I had to make that power my own.\""
        imageUri = "https://cards.scryfall.io/normal/front/1/0/100c3b67-0c92-4224-b5ed-67789c612df7.jpg?1789470892"
        inBooster = false
    }
}
