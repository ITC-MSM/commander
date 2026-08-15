package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Savanti Romero, Time's Exile
 * {3}{B}{B}
 * Legendary Creature — Demon Wizard
 * 4/4
 *
 * Trample
 * At the beginning of combat on your turn, put a +1/+1 counter on
 * Savanti Romero. Then you draw X cards and lose X life, where X is
 * the number of counters on Savanti Romero.
 */
val SavantiRomeroTimesExile = card("Savanti Romero, Time's Exile") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Demon Wizard"
    oracleText = "Trample\nAt the beginning of combat on your turn, put a +1/+1 counter on Savanti Romero. Then you draw X cards and lose X life, where X is the number of counters on Savanti Romero."
    power = 4
    toughness = 4

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.BeginCombat
        val counterAmount = DynamicAmount.EntityProperty(
            EntityReference.Source,
            EntityNumericProperty.CounterCount(CounterTypeFilter.Any)
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
            .then(Effects.DrawCards(counterAmount))
            .then(Effects.LoseLife(counterAmount, EffectTarget.Controller))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "72"
        artist = "Michele Giorgi"
        flavorText = "\"When I am done, there will be no evidence that you ever existed!\""
        imageUri = "https://cards.scryfall.io/normal/front/0/1/01cb8ded-7f77-4b75-b799-23e9f5efb513.jpg?1769005872"
    }
}
