package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Seedguide Ash
 * {4}{G}
 * Creature — Treefolk Druid
 * 4/4
 * When this creature dies, you may search your library for up to three Forest cards, put them
 * onto the battlefield tapped, then shuffle.
 *
 * "Forest cards" — not *basic* Forest cards — so the filter is any land with the Forest subtype;
 * a dual such as Stomping Ground qualifies. The whole-trigger "you may" is `optional = true`
 * rather than a wrapped `MayEffect`, and `SelectionMode.ChooseUpTo` is what `searchLibrary`'s
 * `count` already means, so "up to three" needs nothing extra.
 */
val SeedguideAsh = card("Seedguide Ash") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Treefolk Druid"
    power = 4
    toughness = 4
    oracleText = "When this creature dies, you may search your library for up to three Forest cards, " +
        "put them onto the battlefield tapped, then shuffle."

    triggeredAbility {
        trigger = Triggers.Dies
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Land.withSubtype(Subtype.FOREST),
            count = 3,
            destination = SearchDestination.BATTLEFIELD,
            entersTapped = true,
            shuffleAfter = true
        )
        description = "you may search your library for up to three Forest cards, put them onto the " +
            "battlefield tapped, then shuffle."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "235"
        artist = "John Avon"
        flavorText = "\"May you shade three generations of seedlings.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/c/eca605c0-6270-4238-bb77-0bbfd7568807.jpg?1783942856"
    }
}
