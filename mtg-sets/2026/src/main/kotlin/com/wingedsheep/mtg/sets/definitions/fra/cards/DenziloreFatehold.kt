package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val DenziloreFatehold = card("Denzilore Fatehold") {
    manaCost = "{1}{W}{U}{U}"
    colorIdentity = "UW"
    typeLine = "Legendary Creature — Elder Sphinx"
    oracleText = "Flash\nFlying\nWhenever you scry or surveil, put a +1/+1 counter on each creature you control."
    power = 3
    toughness = 4

    keywords(Keyword.FLASH, Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.WheneverYouScryOrSurveil
        effect = Effects.ForEachInGroup(
            GroupFilter.AllCreaturesYouControl,
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "128"
        artist = "Dmitry Burmak"
        imageUri = "https://cards.scryfall.io/normal/front/9/8/986f9e98-9d8d-428b-9187-860745cf3269.jpg?1788878215"
        inBooster = false
    }
}
