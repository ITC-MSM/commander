package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Boggart Harbinger
 * {2}{B}
 * Creature — Goblin Shaman
 * 2/1
 * When this creature enters, you may search your library for a Goblin card, reveal it, then
 * shuffle and put that card on top.
 *
 * One of Lorwyn's five (plus two) Harbingers: the same tutor-to-top with a different tribe. The
 * "you may" is the `ChooseUpTo(1)` inside [Patterns.Library.searchLibrary] — declining, or having
 * no Goblin card, simply moves nothing. The filter is a *card* filter (not a creature filter), so
 * Kindred cards like Tarfire are found too.
 */
val BoggartHarbinger = card("Boggart Harbinger") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Goblin Shaman"
    power = 2
    toughness = 1
    oracleText = "When this creature enters, you may search your library for a Goblin card, reveal it, " +
        "then shuffle and put that card on top."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.withSubtype(Subtype.GOBLIN),
            count = 1,
            destination = SearchDestination.TOP_OF_LIBRARY,
            shuffleAfter = true,
            reveal = true
        )
        description = "you may search your library for a Goblin card, reveal it, then shuffle and " +
            "put that card on top."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "102"
        artist = "Steve Prescott"
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8ea8d2b8-bcac-410d-aa5a-e4a74f7d5315.jpg?1783942893"
    }
}
