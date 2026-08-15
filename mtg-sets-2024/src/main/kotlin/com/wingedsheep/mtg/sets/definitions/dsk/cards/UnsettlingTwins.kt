package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Unsettling Twins
 * {3}{W}
 * Creature — Human
 * 2/2
 * When this creature enters, manifest dread. (Look at the top two cards of your library. Put one
 * onto the battlefield face down as a 2/2 creature and the other into your graveyard. Turn it face
 * up any time for its mana cost if it's a creature card.)
 *
 * Proves the triggered "manifest dread" path (CR 701.62b): the keyword action runs as an
 * enters-the-battlefield trigger, reusing the shared [Patterns.Library.manifestDread] recipe.
 */
val UnsettlingTwins = card("Unsettling Twins") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human"
    oracleText = "When this creature enters, manifest dread. (Look at the top two cards of your " +
        "library. Put one onto the battlefield face down as a 2/2 creature and the other into your " +
        "graveyard. Turn it face up any time for its mana cost if it's a creature card.)"
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.manifestDread()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "38"
        artist = "Lauren K. Cannon"
        imageUri = "https://cards.scryfall.io/normal/front/8/5/85da2e65-4e22-48b6-98ad-6969684d69e1.jpg?1726286000"
    }
}
