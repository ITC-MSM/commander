package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Goblin Matron
 * {2}{R}
 * Creature — Goblin
 * 1/1
 * When this creature enters, you may search your library for a Goblin card, reveal that card,
 * put it into your hand, then shuffle.
 *
 * Portal Second Age is the card's earliest real-expansion printing, so the canonical
 * [com.wingedsheep.sdk.model.CardDefinition] lives here — Urza's Saga and Modern Horizons are
 * reprints.
 *
 * A "Goblin card" is a card with the Goblin creature type, not a card that merely mentions or
 * makes Goblins. Declining the "may" skips the shuffle too, which is why the consent gate wraps
 * the whole search rather than sitting inside it.
 *
 * The filter is `Any.withSubtype`, **not** `Permanent.withSubtype`, and the difference is live
 * rather than pedantic: Tarfire is a `Kindred Instant — Goblin`, so it is a Goblin card that is not
 * a permanent, and fetching it is a real Lorwyn-era line. Argentum Assay currently reads the phrase
 * the narrower way and so reports this card as DIVERGENT; the card is right and the parser is not —
 * see the PR body for the measurements.
 */
val GoblinMatron = card("Goblin Matron") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin"
    power = 1
    toughness = 1
    oracleText = "When this creature enters, you may search your library for a Goblin card, " +
        "reveal that card, put it into your hand, then shuffle."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = MayEffect(
            Patterns.Library.searchLibrary(
                filter = GameObjectFilter.Any.withSubtype(Subtype.GOBLIN),
                count = 1,
                destination = SearchDestination.HAND,
                reveal = true,
                shuffleAfter = true,
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "100"
        artist = "Daniel Gelon"
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f99dc21c-8600-49bf-b0a3-c981f7ec7ac3.jpg?1783946465"
        ruling(
            "2019-06-14",
            "If an effect refers to a “[subtype] spell” or “[subtype] card,” it refers only to a " +
                "spell or card that has that subtype. For example, Goblin War Party is a card that references " +
                "and creates Goblins, but it isn't a Goblin card.",
        )
        ruling(
            "2004-10-04",
            "Because the “search” requires you to find a card with certain characteristics, you don't " +
                "have to find the card if you don't want to.",
        )
    }
}
