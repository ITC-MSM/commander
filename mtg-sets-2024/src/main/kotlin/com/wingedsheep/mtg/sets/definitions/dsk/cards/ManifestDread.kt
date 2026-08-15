package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Manifest Dread
 * {1}{G}
 * Sorcery
 * Manifest dread. (Look at the top two cards of your library. Put one onto the battlefield face
 * down as a 2/2 creature and the other into your graveyard. Turn it face up any time for its mana
 * cost if it's a creature card.)
 *
 * The eponymous card for the Manifest Dread keyword action (CR 701.62). Composes the shared
 * [Patterns.Library.manifestDread] recipe — no card-specific wiring.
 */
val ManifestDread = card("Manifest Dread") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Manifest dread. (Look at the top two cards of your library. Put one onto the " +
        "battlefield face down as a 2/2 creature and the other into your graveyard. Turn it face " +
        "up any time for its mana cost if it's a creature card.)"

    spell {
        effect = Patterns.Library.manifestDread()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "189"
        artist = "Andrey Kuzinskiy"
        flavorText = "\"That's why I never travel without a flamethrower.\"\n—Rip, spawn hunter"
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a649265b-6c32-49e7-b6cb-6086c40d26e8.jpg?1726286570"
    }
}
