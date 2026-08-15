package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Prishe's Wanderings
 * {2}{G}
 * Instant
 * Search your library for a basic land card or Town card, put it onto the battlefield
 * tapped, then shuffle. When you search your library this way, put a +1/+1 counter on
 * target creature you control.
 *
 * Resolving the spell always performs the search, so the reflexive "when you search …"
 * counter is modeled as a straight follow-up effect on the spell's target creature. The
 * spell requires a legal target creature you control to be cast.
 */
val PrishesWanderings = card("Prishe's Wanderings") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Search your library for a basic land card or Town card, put it onto the battlefield tapped, then shuffle. When you search your library this way, put a +1/+1 counter on target creature you control."

    val basicOrTown = GameObjectFilter.BasicLand or GameObjectFilter.Land.withSubtype("Town")

    spell {
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.Composite(
            listOf(
                Patterns.Library.searchLibrary(
                    filter = basicOrTown,
                    count = 1,
                    destination = SearchDestination.BATTLEFIELD,
                    entersTapped = true
                ),
                Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, creature)
            )
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "193"
        artist = "Daniel Correia"
        flavorText = "\"There's only one reason. They smell an adventure! C'mon! We've got no time to lose!\""
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d6e1dee0-e2cd-4899-a3ea-7d0df717c9ab.jpg?1748706482"
    }
}
