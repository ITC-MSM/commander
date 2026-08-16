package com.wingedsheep.mtg.sets.definitions.mh3.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Faithful Watchdog
 * {G}{W}
 * Creature — Dog
 * 0/0
 *
 * Vigilance
 * This creature enters with three +1/+1 counters on it.
 *
 * The printed P/T really is 0/0 — the body is entirely the three counters, so the "enters with"
 * clause must be a replacement effect ([EntersWithCounters], CR 121.6 / 614.1c) and *not* an ETB
 * triggered ability. A trigger would let the creature exist as a 0/0 for a moment and die to state-
 * based actions (CR 704.5f) before the counters ever landed. `selfOnly` scopes the replacement to
 * this permanent.
 */
val FaithfulWatchdog = card("Faithful Watchdog") {
    manaCost = "{G}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Dog"
    power = 0
    toughness = 0
    oracleText = "Vigilance\nThis creature enters with three +1/+1 counters on it."

    keywords(Keyword.VIGILANCE)

    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.PlusOnePlusOne,
            count = 3,
            selfOnly = true
        )
    )

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "185"
        artist = "Samuel Perin"
        flavorText = "Though its official designation was Third Battalion Patrol Hound 7, the " +
            "soldiers dubbed their new companion Lord Fancy-Paws."
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b9afac99-a094-41a8-8323-90dec29691c4.jpg?1783911251"
    }
}
