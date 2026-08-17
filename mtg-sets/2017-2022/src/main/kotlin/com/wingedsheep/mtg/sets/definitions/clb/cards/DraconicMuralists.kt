package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Draconic Muralists
 * {3}{G}
 * Creature — Dragon Bard
 * 4/3
 * When this creature dies, you may search your library for a Dragon card, reveal it, put it into your hand, then shuffle.
 *
 * A dies trigger over the stock tutor recipe: [Patterns.Library.searchLibrary] is the whole
 * gather / select / reveal-and-move / shuffle pipeline, so only the destination and the reveal are
 * spelled here. "A Dragon card" is a bare tribal noun — any permanent card with the subtype, not
 * only a creature — and the printed "you may" is the ability's `optional` flag, which the builder
 * lowers into the consent gate around the search.
 */
val DraconicMuralists = card("Draconic Muralists") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dragon Bard"
    power = 4
    toughness = 3
    oracleText = "When this creature dies, you may search your library for a Dragon card, reveal it, put it into your hand, then shuffle."

    triggeredAbility {
        trigger = Triggers.Dies
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Permanent.withSubtype(Subtype.DRAGON),
            count = 1,
            destination = SearchDestination.HAND,
            shuffleAfter = true,
            reveal = true
        )
        description = "When this creature dies, you may search your library for a Dragon card, " +
            "reveal it, put it into your hand, then shuffle."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "224"
        artist = "Tom Babbey"
        flavorText = "\"We must never allow the deeds of our great ancestors to be forgotten.\""
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4c9bf864-1f93-4d73-a212-f71265411768.jpg?1783922717"
    }
}
