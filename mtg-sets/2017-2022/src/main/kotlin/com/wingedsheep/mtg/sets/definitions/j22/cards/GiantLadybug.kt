package com.wingedsheep.mtg.sets.definitions.j22.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Giant Ladybug
 * {2}{G}
 * Creature — Insect
 * 4/1
 * Reach
 * When this creature enters, you may search your library for a basic land card, reveal it, then shuffle and put that card on top.
 */
val GiantLadybug = card("Giant Ladybug") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect"
    power = 4
    toughness = 1
    oracleText = "Reach\nWhen this creature enters, you may search your library for a basic land card, reveal it, then shuffle and put that card on top."

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            destination = SearchDestination.TOP_OF_LIBRARY,
            reveal = true
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "39"
        artist = "Marta Nael"
        flavorText = "It's bad luck to step on one, but worse luck to have one step on you."
        imageUri = "https://cards.scryfall.io/normal/front/6/7/67f55ef7-8479-4d56-9801-cb4fd5526e73.jpg?1783919180"
    }
}
