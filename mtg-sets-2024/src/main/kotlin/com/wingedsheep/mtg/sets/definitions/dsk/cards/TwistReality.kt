package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetSpell

/**
 * Twist Reality
 * {1}{U}{U}
 * Instant
 * Choose one —
 * • Counter target spell.
 * • Manifest dread. (Look at the top two cards of your library. Put one onto the battlefield
 *   face down as a 2/2 creature and the other into your graveyard. Turn it face up any time
 *   for its mana cost if it's a creature card.)
 */
val TwistReality = card("Twist Reality") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Counter target spell.\n" +
        "• Manifest dread. (Look at the top two cards of your library. Put one onto the " +
        "battlefield face down as a 2/2 creature and the other into your graveyard. Turn it " +
        "face up any time for its mana cost if it's a creature card.)"
    spell {
        modal(chooseCount = 1) {
            mode("Counter target spell") {
                target("target", TargetSpell())
                effect = Effects.CounterSpell()
            }
            mode(
                "Manifest dread. (Look at the top two cards of your library. Put one onto the " +
                    "battlefield face down as a 2/2 creature and the other into your graveyard. Turn " +
                    "it face up any time for its mana cost if it's a creature card.)"
            ) {
                effect = Patterns.Library.manifestDread()
            }
        }
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "77"
        artist = "Allen Williams"
        imageUri = "https://cards.scryfall.io/normal/front/6/4/644714dd-7a0b-4d4b-9a61-8f6e505b1d22.jpg?1726286137"
    }
}
