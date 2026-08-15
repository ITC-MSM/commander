package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Winnow
 * {1}{W}
 * Instant
 * Destroy target nonland permanent if another permanent with the same name is on the
 * battlefield.
 * Draw a card.
 *
 * The destruction is gated by [Conditions.AnotherPermanentWithSameNameAsTarget], evaluated
 * against the chosen target at resolution. The "Draw a card" clause is unconditional, so it
 * happens whether or not the permanent is destroyed.
 */
val Winnow = card("Winnow") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Destroy target nonland permanent if another permanent with the same name " +
        "is on the battlefield.\n" +
        "Draw a card."

    spell {
        val permanent = target("target nonland permanent", Targets.NonlandPermanent)
        effect = ConditionalEffect(
            condition = Conditions.AnotherPermanentWithSameNameAsTarget(),
            effect = Effects.Destroy(permanent),
        ) then Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "45"
        artist = "Roger Raupp"
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d61748dd-4010-47da-8717-ca0147877057.jpg?1562937982"
    }
}
