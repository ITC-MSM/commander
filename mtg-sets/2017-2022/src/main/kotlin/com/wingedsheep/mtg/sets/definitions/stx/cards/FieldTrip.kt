package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Field Trip — Strixhaven: School of Mages #131 (canonical printing)
 * {2}{G} · Sorcery
 *
 * Search your library for a basic Forest card, put that card onto the battlefield tapped, then
 * shuffle.
 * Learn.
 *
 * `Patterns.Library.searchLibrary` is the gather → select → move pipeline: `ChooseUpTo(1)` is what
 * makes finding a card optional (CR 701.23b — you may fail to find), `entersTapped = true` is the
 * printed "tapped", and `shuffleAfter = true` is the "then shuffle".
 *
 * The two pipelines nest safely: the search stores under `searchable` / `found` and Learn under
 * `learn_hand` / `learn_discarded`, so neither clobbers the other's collections.
 *
 * `Learn` is [Patterns.Mechanic.learn] (CR 701.48).
 */
val FieldTrip = card("Field Trip") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Search your library for a basic Forest card, put that card onto the battlefield " +
        "tapped, then shuffle.\n" +
        "Learn. (You may reveal a Lesson card you own from outside the game and put it into your " +
        "hand, or discard a card to draw a card.)"

    spell {
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand.withSubtype(Subtype.FOREST),
            count = 1,
            destination = SearchDestination.BATTLEFIELD,
            entersTapped = true,
            shuffleAfter = true
        ) then Patterns.Mechanic.learn()
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "131"
        artist = "Piotr Dura"
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f235060a-eb49-4a73-bb5f-01228c3c4070.jpg?1783927343"
    }
}
