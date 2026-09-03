package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.PlayersRevealTopOfLibrary

/**
 * Wizened Snitches
 * {3}{U}
 * Creature — Faerie Rogue
 * 1/3
 *
 * Flying
 * Players play with the top card of their libraries revealed.
 *
 * The symmetric form of Goblin Spy's [com.wingedsheep.sdk.scripting.RevealTopOfLibrary]:
 * [PlayersRevealTopOfLibrary] opens *every* player's top card to *every* player, including the
 * libraries of players who control no permanent with the ability. Like its sibling it grants no
 * permission to play the revealed card — the top card is public information and nothing more, so
 * it still can't be cycled, suspended, discarded, or have its activated abilities used from there.
 */
val WizenedSnitches = card("Wizened Snitches") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Faerie Rogue"
    power = 1
    toughness = 3
    oracleText = "Flying\nPlayers play with the top card of their libraries revealed."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = PlayersRevealTopOfLibrary
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "75"
        artist = "Greg Staples"
        flavorText = "\"Be careful what information you ask them to find, for it will likely be " +
            "the subject of tale time at the alehouse.\"\n—Sirislav, Dimir spy"
        imageUri = "https://cards.scryfall.io/normal/front/b/f/bf35807a-a003-4a99-a883-03b8e097f1b2.jpg?1783943675"
        ruling(
            "2013-04-15",
            "The top card of your library isn't in your hand, so you can't suspend it, cycle it, " +
                "discard it, or activate any of its activated abilities."
        )
        ruling(
            "2013-04-15",
            "If the top card of your library changes while you're casting a spell, playing a land, " +
                "or activating an ability, the new top card won't be revealed until you finish doing so."
        )
        ruling(
            "2013-04-15",
            "When playing with the top card of your library revealed, if an effect tells you to draw " +
                "several cards, reveal each one before you draw it."
        )
    }
}
