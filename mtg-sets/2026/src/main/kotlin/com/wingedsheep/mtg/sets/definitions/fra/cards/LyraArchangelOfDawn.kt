package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val LyraArchangelOfDawn = card("Lyra, Archangel of Dawn") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Legendary Creature — Angel Knight"
    power = 3
    toughness = 3
    oracleText = "Flying\nWhenever you gain life, put a +1/+1 counter on each Angel you control."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouGainLife
        // "each Angel you control" is a bare tribal noun, so it names permanents (not only
        // creatures), and it includes Lyra herself.
        effect = Effects.ForEachInGroup(
            filter = GroupFilter(GameObjectFilter.Permanent.withSubtype(Subtype.ANGEL).youControl()),
            effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "201"
        artist = "Victor Adame Minguez"
        flavorText = "She is the radiant sun that shatters darkness."
        imageUri = "https://cards.scryfall.io/normal/front/8/6/86a3866e-68a8-402c-baf0-1908e98e3995.jpg?1789127693"
        inBooster = false
    }
}
