package com.wingedsheep.mtg.sets.definitions.snc.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithCounters
import com.wingedsheep.sdk.scripting.events.CounterTypeFilter

/**
 * Disciplined Duelist
 * {G}{W}{U}
 * Creature — Human Citizen
 * 2 / 1
 * Double strike
 * This creature enters with a shield counter on it. (If it would be dealt damage or destroyed, remove a shield counter from it instead.)
 *
 * The shield counter is the engine's CR 122.1c counter — its replacement + prevention pair is
 * wired at the damage and destroy chokepoints, so entering with one is the plain
 * [EntersWithCounters] as-enters replacement and nothing else is needed card-side.
 */
val DisciplinedDuelist = card("Disciplined Duelist") {
    manaCost = "{G}{W}{U}"
    colorIdentity = "GUW"
    typeLine = "Creature — Human Citizen"
    oracleText = "Double strike\nThis creature enters with a shield counter on it. (If it would be dealt damage or destroyed, remove a shield counter from it instead.)"
    power = 2
    toughness = 1

    keywords(Keyword.DOUBLE_STRIKE)

    replacementEffect(
        EntersWithCounters(
            counterType = CounterTypeFilter.Named(Counters.SHIELD),
            count = 1,
            selfOnly = true,
        )
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "182"
        artist = "Josu Hernaiz"
        flavorText = "Brokers agents defend their charges by all means available, from intricate logic to blunt force trauma."
        imageUri = "https://cards.scryfall.io/normal/front/e/2/e23cdb0a-e114-47f8-aceb-c54c4683bbc5.jpg?1783923087"
    }
}
