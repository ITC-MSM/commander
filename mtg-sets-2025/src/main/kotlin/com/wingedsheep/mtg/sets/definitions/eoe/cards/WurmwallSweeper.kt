package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantCardType
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.station

/**
 * Wurmwall Sweeper
 * {2}
 * Artifact — Spacecraft
 * When this Spacecraft enters, surveil 2.
 * Station (Tap another creature you control: Put charge counters equal to its power on this Spacecraft. Station only as a sorcery. It's an artifact creature at 4+.)
 * 4+ | Flying
 * 2/2
 */
val WurmwallSweeper = card("Wurmwall Sweeper") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact — Spacecraft"
    power = 2
    toughness = 2
    oracleText = "When this Spacecraft enters, surveil 2.\nStation (Tap another creature you control: Put charge counters equal to its power on this Spacecraft. Station only as a sorcery. It's an artifact creature at 4+.)\n4+ | Flying"

    // ETB: surveil 2
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.surveil(2)
    }

    // Station activated ability: tap another creature → add charge counters equal to its power
    station()

    // 4+ charge counters: becomes artifact creature and gains flying
    val charge4 = Conditions.SourceCounterCountAtLeast(Counters.CHARGE, 4)

    staticAbility {
        condition = charge4
        ability = GrantCardType("CREATURE", GroupFilter.source())
    }

    staticAbility {
        condition = charge4
        ability = GrantKeyword(Keyword.FLYING, GroupFilter.source())
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "249"
        artist = "Hardy Fowler"
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9ace282a-5901-4d36-ad21-17eb88bc5138.jpg?1755341422"
    }
}
