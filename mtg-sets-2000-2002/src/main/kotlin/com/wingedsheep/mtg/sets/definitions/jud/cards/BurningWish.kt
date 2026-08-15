package com.wingedsheep.mtg.sets.definitions.jud.cards

import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Burning Wish {1}{R}
 * Sorcery
 *
 * You may reveal a sorcery card you own from outside the game and put it into your hand.
 * Exile Burning Wish.
 *
 * The archetypal "wish" (CR 100.4 / 400.11a — "outside the game" = the player's sideboard,
 * modelled as the private [com.wingedsheep.sdk.core.Zone.SIDEBOARD]). It is pure composition over
 * the Gather → Select → Move pipeline via [Patterns.Sideboard.wish]: gather the sorcery cards in
 * the controller's sideboard, let them choose up to one (the "may"), reveal it, and put it into
 * their hand. No new effect or executor.
 *
 * "Exile Burning Wish." is [selfExile] — the spell goes to exile instead of the graveyard on
 * resolution (CR 608.2g routes the spell to exile; the engine flag is `CardScript.selfExileOnResolve`).
 */
val BurningWish = card("Burning Wish") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "You may reveal a sorcery card you own from outside the game and put it into " +
        "your hand. Exile Burning Wish."

    spell {
        selfExile()
        effect = Patterns.Sideboard.wish(Filters.Sorcery)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "83"
        artist = "Scott M. Fischer"
        flavorText = "She wished for a weapon, but not for the skill to wield it."
        imageUri = "https://cards.scryfall.io/normal/front/1/c/1c9b692a-e832-4612-a6ec-93b52f6a0410.jpg?1562628871"
    }
}
