package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Demolition Stomper
 * {6}
 * Artifact — Vehicle
 * 10/7
 * This Vehicle can't be blocked by creatures with power 2 or less.
 * Crew 5
 *
 * The evasion is the unified [CantBeBlockedBy] over `Creature.powerAtMost(2)`. Crew is engine-live:
 * [KeywordAbility.crew] is the whole implementation, and the `Vehicle` subtype on the type line is
 * what makes the printed P/T a crewed-only body.
 */
val DemolitionStomper = card("Demolition Stomper") {
    manaCost = "{6}"
    typeLine = "Artifact — Vehicle"
    oracleText = "This Vehicle can't be blocked by creatures with power 2 or less.\n" +
        "Crew 5 (Tap any number of creatures you control with total power 5 or more: This Vehicle becomes an artifact creature until end of turn.)"
    power = 10
    toughness = 7

    staticAbility {
        ability = CantBeBlockedBy(GameObjectFilter.Creature.powerAtMost(2))
    }

    keywordAbility(KeywordAbility.crew(5))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "206"
        artist = "James Paick"
        imageUri = "https://cards.scryfall.io/normal/front/e/9/e9af13a0-a9c1-454c-992d-ce79ff161187.jpg?1783937159"
    }
}
