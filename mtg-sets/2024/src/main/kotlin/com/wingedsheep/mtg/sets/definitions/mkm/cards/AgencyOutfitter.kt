package com.wingedsheep.mtg.sets.definitions.mkm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Agency Outfitter — Murders at Karlov Manor #38
 * {4}{U}{U} · Creature — Sphinx Detective · 4/3 · Uncommon
 *
 * The two named-card clauses are independent: the controller may find neither, either, or both,
 * but never two copies of the same named card. Two one-card multi-zone searches preserve that
 * constraint while allowing each card to come from the graveyard, hand, or library.
 */
val AgencyOutfitter = card("Agency Outfitter") {
    manaCost = "{4}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Sphinx Detective"
    oracleText = "Flying\n" +
        "When this creature enters, you may search your graveyard, hand and/or library for a card " +
        "named Magnifying Glass and/or a card named Thinking Cap and put them onto the battlefield. " +
        "If you search your library this way, shuffle."
    power = 4
    toughness = 3

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.searchMultipleZones(
            zones = listOf(Zone.GRAVEYARD, Zone.HAND, Zone.LIBRARY),
            filter = GameObjectFilter.Any.named("Magnifying Glass"),
            count = 1,
            destination = SearchDestination.BATTLEFIELD,
        ) then Patterns.Library.searchMultipleZones(
            zones = listOf(Zone.GRAVEYARD, Zone.HAND, Zone.LIBRARY),
            filter = GameObjectFilter.Any.named("Thinking Cap"),
            count = 1,
            destination = SearchDestination.BATTLEFIELD,
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "38"
        artist = "Andrew Mar"
        imageUri = "https://cards.scryfall.io/normal/front/8/1/8112f133-535e-4264-8357-9cbf97957710.jpg?1783912917"
    }
}
