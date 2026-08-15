package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantCardType
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.station

/**
 * Galvanizing Sawship
 * {5}{R}
 * Artifact — Spacecraft
 * Station (Tap another creature you control: Put charge counters equal to its power on this Spacecraft. Station only as a sorcery. It's an artifact creature at 3+.)
 * 3+ | Flying, haste
 * 6/5
 */
val GalvanizingSawship = card("Galvanizing Sawship") {
    manaCost = "{5}{R}"
    colorIdentity = "R"
    typeLine = "Artifact — Spacecraft"
    power = 6
    toughness = 5
    oracleText = "Station (Tap another creature you control: Put charge counters equal to its power on this Spacecraft. Station only as a sorcery. It's an artifact creature at 3+.)\n3+ | Flying, haste"

    // Station activated ability: tap another creature → add charge counters equal to its power
    station()

    // Conditional type change: artifact creature at 3+ charge counters
    staticAbility {
        condition = Conditions.SourceCounterCountAtLeast(Counters.CHARGE, 3)
        ability = GrantCardType("CREATURE", GroupFilter.source())
    }

    // Conditional keywords: flying and haste at 3+ charge counters
    staticAbility {
        condition = Conditions.SourceCounterCountAtLeast(Counters.CHARGE, 3)
        ability = GrantKeyword(Keyword.FLYING.name, GroupFilter.source())
    }

    staticAbility {
        condition = Conditions.SourceCounterCountAtLeast(Counters.CHARGE, 3)
        ability = GrantKeyword(Keyword.HASTE.name, GroupFilter.source())
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "136"
        artist = "Constantin Marin"
        imageUri = "https://cards.scryfall.io/normal/front/5/b/5bbce9fb-401f-4e78-acd5-9d3b506687fd.jpg?1755341407"
    }
}
