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
 * Uthros Scanship
 * {3}{U}
 * Artifact — Spacecraft
 * When this Spacecraft enters, draw two cards, then discard a card.
 * Station (Tap another creature you control: Put charge counters equal to its power on this Spacecraft. Station only as a sorcery. It's an artifact creature at 8+.)
 * 8+ | Flying
 * 4/4
 */
val UthrosScanship = card("Uthros Scanship") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Artifact — Spacecraft"
    power = 4
    toughness = 4
    oracleText = "When this Spacecraft enters, draw two cards, then discard a card.\nStation (Tap another creature you control: Put charge counters equal to its power on this Spacecraft. Station only as a sorcery. It's an artifact creature at 8+.)\n8+ | Flying"

    // ETB: draw two cards, then discard a card
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Hand.loot(draw = 2, discard = 1)
    }

    // Station activated ability: tap another creature → add charge counters equal to its power
    station()

    // 8+ charge counters: becomes artifact creature and gains flying
    val charge8 = Conditions.SourceCounterCountAtLeast(Counters.CHARGE, 8)

    staticAbility {
        condition = charge8
        ability = GrantCardType("CREATURE", GroupFilter.source())
    }

    staticAbility {
        condition = charge8
        ability = GrantKeyword(Keyword.FLYING, GroupFilter.source())
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "85"
        artist = "Sergey Glushakov"
        imageUri = "https://cards.scryfall.io/normal/front/1/f/1f93887f-35c5-472f-83d0-54227b3bd1d2.jpg?1755341393"
    }
}
