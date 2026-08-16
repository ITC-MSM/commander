package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Adventurous Impulse
 * {G}
 * Sorcery
 * Look at the top three cards of your library. You may reveal a creature or land card
 * from among them and put it into your hand. Put the rest on the bottom of your library
 * in any order.
 */
val AdventurousImpulse = card("Adventurous Impulse") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Look at the top three cards of your library. You may reveal a creature or land card from among them and put it into your hand. Put the rest on the bottom of your library in any order."

    spell {
        effect = Patterns.Library.lookAtTopRevealMatchingToHand(
            count = DynamicAmount.Fixed(3),
            filter = GameObjectFilter.Creature or GameObjectFilter.Land,
            prompt = "You may reveal a creature or land card and put it into your hand",
            restOrder = CardOrder.ControllerChooses
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "153"
        artist = "Titus Lunter"
        flavorText = "\"Every odyssey begins with a single step.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/4/f426c92c-6e71-49f0-9a91-0d529bf8c17d.jpg?1562745600"
    }
}
