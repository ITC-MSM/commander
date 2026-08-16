package com.wingedsheep.mtg.sets.definitions.one.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Myr Kinsmith
 * {4}
 * Artifact Creature — Myr
 * 3/1
 *
 * When this creature enters, you may search your library for a Myr card, reveal it, put it into your hand, then shuffle.
 */
val MyrKinsmith = card("Myr Kinsmith") {
    manaCost = "{4}"
    typeLine = "Artifact Creature — Myr"
    power = 3
    toughness = 1
    oracleText = "When this creature enters, you may search your library for a Myr card, reveal it, put it into your hand, then shuffle."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Permanent.withSubtype("Myr"),
            destination = SearchDestination.HAND,
            reveal = true
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "236"
        artist = "Cristi Balanescu"
        imageUri = "https://cards.scryfall.io/normal/front/6/0/6046e50a-f4c9-4029-a589-62a19371b734.jpg?1783917989"
    }
}
