package com.wingedsheep.mtg.sets.definitions.iko.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Farfinder
 * {3}
 * Creature — Fox
 * 1/1
 * Vigilance
 * When this creature enters, you may search your library for a basic land card, reveal it, put it
 * into your hand, then shuffle.
 *
 * `optional = true` lowers to the CR 601-style consent gate around the whole search, so a
 * controller who declines never opens the library — which matters, because searching is a public
 * act (CR 701.23) and the recipe emits the "a player searched their library" event either way.
 */
val Farfinder = card("Farfinder") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Creature — Fox"
    power = 1
    toughness = 1
    oracleText = "Vigilance\n" +
        "When this creature enters, you may search your library for a basic land card, reveal it, " +
        "put it into your hand, then shuffle."

    keywords(Keyword.VIGILANCE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            destination = SearchDestination.HAND,
            reveal = true
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "2"
        artist = "Leesha Hannigan"
        flavorText = "\"Take us home,\" she whispered, and the fox's eyes began to glow."
        imageUri = "https://cards.scryfall.io/normal/front/5/3/53f63f40-46f1-4b9e-b447-ff9274f2b926.jpg"
    }
}
