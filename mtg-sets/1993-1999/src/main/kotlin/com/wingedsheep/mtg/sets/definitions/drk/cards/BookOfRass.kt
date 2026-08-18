package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Book of Rass
 * {6}
 * Artifact — Book
 * {2}, Pay 2 life: Draw a card.
 */
val BookOfRass = card("Book of Rass") {
    manaCost = "{6}"
    colorIdentity = ""
    typeLine = "Artifact — Book"
    oracleText = "{2}, Pay 2 life: Draw a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.PayLife(2))
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "98"
        artist = "Sandra Everingham"
        imageUri = "https://cards.scryfall.io/normal/front/5/a/5a391ada-e9e3-45db-ae84-17421ac6b44d.jpg?1783947929"

        ruling("2004-10-04", "You can't spend yourself to below zero life. You can't spend life you don't have.")
    }
}
