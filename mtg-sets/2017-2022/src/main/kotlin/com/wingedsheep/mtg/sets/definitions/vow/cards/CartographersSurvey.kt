package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.SelectionMode
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Cartographer's Survey
 * {3}{G}
 * Sorcery
 *
 * Look at the top seven cards of your library. Put up to two land cards from among them onto the
 * battlefield tapped. Put the rest on the bottom of your library in a random order.
 *
 * `Patterns.Library.lookAtTopAndTakeMatching` is this sentence, parameter for parameter: privately
 * look at the top seven, let the caster put *up to two* land cards among them onto the battlefield
 * tapped ([SelectionMode.ChooseUpTo] filtered to [Filters.Land]; the recipe shows every looked-at
 * card and only the lands are selectable), and put the remainder on the bottom of the library in a
 * random order ([CardOrder.Random]). The card used to restate those four steps by hand.
 */
val CartographersSurvey = card("Cartographer's Survey") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Look at the top seven cards of your library. Put up to two land cards from among " +
        "them onto the battlefield tapped. Put the rest on the bottom of your library in a random order."

    spell {
        effect = Patterns.Library.lookAtTopAndTakeMatching(
            count = DynamicAmount.Fixed(7),
            filter = Filters.Land,
            prompt = "Put up to two land cards from among them onto the battlefield tapped",
            selection = SelectionMode.ChooseUpTo(DynamicAmount.Fixed(2)),
            keepDestination = CardDestination.ToZone(Zone.BATTLEFIELD, placement = ZonePlacement.Tapped),
            restDestination = CardDestination.ToZone(Zone.LIBRARY, placement = ZonePlacement.Bottom),
            restOrder = CardOrder.Random
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "190"
        artist = "Donato Giancola"
        flavorText = "She monitors the roads in every province, mapping safe routes through hunting " +
            "grounds and haunting grounds alike."
        imageUri = "https://cards.scryfall.io/normal/front/b/9/b9a41cfc-f329-4e69-a785-835f69c7d2ba.jpg?1783924818"
    }
}
