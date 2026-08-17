package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Swooping Protector
 * {3}{W}
 * Creature — Bird Citizen
 * 2 / 1
 * Flash
 * Flying
 * This creature enters with a shield counter on it. (If it would be dealt damage or destroyed, remove a shield counter from it instead.)
 *
 * The shield counter is the engine's CR 122.1c counter — its replacement + prevention pair is
 * wired at the damage and destroy chokepoints, so entering with one is the plain
 * [EntersWithCounters] as-enters replacement and nothing else is needed card-side.
 */
val SwoopingProtector = card("Swooping Protector") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Bird Citizen"
    oracleText = "Flash\nFlying\nThis creature enters with a shield counter on it. (If it would be dealt damage or destroyed, remove a shield counter from it instead.)"
    power = 2
    toughness = 1

    keywords(Keyword.FLASH, Keyword.FLYING)

    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.Named(Counters.SHIELD),
            count = 1,
            selfOnly = true,
        )
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "33"
        artist = "Mark Behm"
        imageUri = "https://cards.scryfall.io/normal/front/8/7/8713498f-a467-4a11-9de2-53a1bbd0b18b.jpg?1783923149"
    }
}
