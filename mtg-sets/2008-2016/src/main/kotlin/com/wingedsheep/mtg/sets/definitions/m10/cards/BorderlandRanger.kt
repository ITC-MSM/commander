package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.SearchDestination

/**
 * Borderland Ranger — Magic 2010 #169
 * {2}{G} · Creature — Human Scout Ranger · 2/2
 *
 * When this creature enters, you may search your library for a basic land card, reveal it, put it
 * into your hand, then shuffle.
 *
 * `optional = true` lowers to the `Gate.MayDecide` the printed "you may" names, wrapping the
 * [Patterns.Library] gather → choose-up-to → move pipeline. `shuffleAfter` is the facade default
 * and supplies the printed "then shuffle"; failing to find is legal (CR 701.23b), which is why the
 * selection is a choose-*up-to*.
 */
val BorderlandRanger = card("Borderland Ranger") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Scout Ranger"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, you may search your library for a basic land card, reveal it, put it " +
        "into your hand, then shuffle."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        optional = true
        effect = Patterns.Library.searchLibrary(
            filter = GameObjectFilter.BasicLand,
            count = 1,
            destination = SearchDestination.HAND,
            reveal = true
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "169"
        artist = "Jesper Ejsing"
        flavorText = "\"Only fools and bandits use roads.\""
        imageUri = "https://cards.scryfall.io/normal/front/b/d/bdd0f8c8-1a1f-4d9b-a6e1-3654f3995012.jpg?1783942366"
    }
}
