package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Primal Command
 * {3}{G}{G}
 * Sorcery
 * Choose two —
 * • Target player gains 7 life.
 * • Put target noncreature permanent on top of its owner's library.
 * • Target player shuffles their graveyard into their library.
 * • Search your library for a creature card, reveal it, put it into your hand, then shuffle.
 *
 * The green member of the Lorwyn Command cycle. Three of the four modes target; the search mode
 * doesn't, and its "reveal it" is the `reveal = true` flag on the standard
 * [Patterns.Library.searchLibrary] recipe rather than a separate reveal step.
 *
 * The 2017-03-14 rulings both fall out of the sequential mode resolution that
 * [AustereCommand] documents — the graveyard shuffle happens after the tuck, so a permanent put on
 * top of a library is shuffled away by a later mode, and Primal Command itself is still on the
 * stack (not yet in a graveyard) when its own shuffle mode runs.
 */
val PrimalCommand = card("Primal Command") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Choose two —\n" +
        "• Target player gains 7 life.\n" +
        "• Put target noncreature permanent on top of its owner's library.\n" +
        "• Target player shuffles their graveyard into their library.\n" +
        "• Search your library for a creature card, reveal it, put it into your hand, then shuffle."

    spell {
        modal(chooseCount = 2) {
            mode("Target player gains 7 life") {
                val player = target("player to gain life", Targets.Player)
                effect = Effects.GainLife(7, player)
            }
            mode("Put target noncreature permanent on top of its owner's library") {
                val permanent = target(
                    "noncreature permanent to tuck",
                    TargetPermanent(filter = TargetFilter.NoncreaturePermanent)
                )
                effect = Effects.PutOnTopOfLibrary(permanent)
            }
            mode("Target player shuffles their graveyard into their library") {
                val player = target("player to shuffle their graveyard", Targets.Player)
                effect = Patterns.Library.shuffleGraveyardIntoLibrary(player)
            }
            mode("Search your library for a creature card, reveal it, put it into your hand, then shuffle") {
                effect = Patterns.Library.searchLibrary(
                    filter = GameObjectFilter.Creature,
                    count = 1,
                    destination = SearchDestination.HAND,
                    reveal = true
                )
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "233"
        artist = "Wayne England"
        imageUri = "https://cards.scryfall.io/normal/front/4/0/40d2c5b9-cef9-4763-8e65-e3b1418c0ad3.jpg?1783942858"
        ruling("2017-03-14", "Primal Command won't be put into your graveyard until after it's finished resolving, which means it won't be shuffled into your library as part of its own effect if you target yourself with its third mode.")
        ruling("2017-03-14", "Primal Command's modes are performed in the order listed. If you put a noncreature permanent on top of its owner's library and have that player shuffle their graveyard into their library, that card is shuffled away.")
    }
}
