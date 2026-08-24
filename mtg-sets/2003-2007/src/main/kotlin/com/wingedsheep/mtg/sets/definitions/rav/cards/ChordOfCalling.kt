package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Chord of Calling
 * {X}{G}{G}{G}
 * Instant
 * Convoke
 * Search your library for a creature card with mana value X or less, put it onto the battlefield,
 * then shuffle.
 *
 * The `X` in the filter is the *cast* X ([GameObjectFilter.manaValueAtMostX]), read off the spell's
 * announced value — convoke pays for it but does not change it.
 *
 * Canonical printing: Ravnica: City of Guilds, the card's earliest real-expansion printing.
 */
val ChordOfCalling = card("Chord of Calling") {
    manaCost = "{X}{G}{G}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText =
        "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Search your library for a creature card with mana value X or less, put it onto the battlefield, then shuffle."

    keywords(Keyword.CONVOKE)

    spell {
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.Creature.manaValueAtMostX(),
            destination = SearchDestination.BATTLEFIELD
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "156"
        artist = "Heather Hudson"
        imageUri = "https://cards.scryfall.io/normal/front/e/0/e064174b-8f07-4fea-9eef-c3b5d0220b1a.jpg?1783943641"
    }
}
