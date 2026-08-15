package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Bind
 * {1}{G}
 * Instant
 *
 * Counter target activated ability. (Mana abilities can't be targeted.)
 * Draw a card.
 *
 * Mana abilities don't use the stack, so they can never be a legal target — the
 * `ActivatedAbility` target only enumerates activated abilities currently on the stack.
 */
val Bind = card("Bind") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Counter target activated ability. (Mana abilities can't be targeted.)\nDraw a card."

    spell {
        target = Targets.ActivatedAbility
        effect = Effects.CounterAbility() then Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "182"
        artist = "Mark Zug"
        imageUri = "https://cards.scryfall.io/normal/front/c/f/cfa51783-9ef8-4e51-ba0d-ce8439d83bdf.jpg?1562936749"
    }
}
