package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Totem-Guide Hartebeest
 * {4}{W}
 * Creature — Antelope
 * 2 / 5
 *
 * When this creature enters, you may search your library for an Aura card, reveal it, put it into your hand, then shuffle.
 *
 * Modeling notes:
 *  - "**When** this creature enters" is the one-shot [Triggers.EntersBattlefield].
 *  - "You **may** search" is a decline on resolution, so `optional = true` lowers the tutor into a
 *    `Gate.MayDecide` — the `Gated` wrapper Assay compiles from this line. It is not a `ChooseUpTo`
 *    "find nothing": that selection freedom is separate, and both are present.
 *  - The search itself is the stock [Patterns.Library.searchLibrary] recipe rather than a
 *    hand-rolled composite — with `destination = HAND` and `reveal = true` it expands to exactly
 *    Assay's Gather → Select(ChooseUpTo 1) → Move(Hand, revealed) → Shuffle → `LibrarySearchedEvent`
 *    chain, and `shuffleAfter` already defaults to true, which is the printed "then shuffle".
 *  - The filter is [Subtype.AURA] alone, with no `IsEnchantment` predicate beside it. Every Aura is
 *    an enchantment, so the narrower filter would select the same cards — but the printed text
 *    restricts by subtype and nothing else, and the extra type predicate is a divergence the Assay
 *    differential flags against the model built from that same text.
 */
val TotemGuideHartebeest = card("Totem-Guide Hartebeest") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Antelope"
    power = 2
    toughness = 5
    oracleText = "When this creature enters, you may search your library for an Aura card, reveal it, put it into your hand, then shuffle."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Any.withSubtype(Subtype.AURA),
            destination = SearchDestination.HAND,
            reveal = true
        )
        description = "When this creature enters, you may search your library for an Aura card, " +
            "reveal it, put it into your hand, then shuffle."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "50"
        artist = "John Avon"
        flavorText = "The kor track hartebeests in hopes of finding magic to help fight the Eldrazi."
        imageUri = "https://cards.scryfall.io/normal/front/1/3/1364a830-36b7-48b9-bf69-6fd35eed9399.jpg?1783942000"
    }
}
