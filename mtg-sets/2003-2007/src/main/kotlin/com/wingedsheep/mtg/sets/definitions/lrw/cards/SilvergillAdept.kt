package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Silvergill Adept
 * {1}{U}
 * Creature — Merfolk Wizard
 * 2/1
 * As an additional cost to cast this spell, reveal a Merfolk card from your hand or pay {3}.
 * When this creature enters, draw a card.
 *
 * The filter is any Merfolk *card*, not a Merfolk creature card — Lorwyn's Kindred noncreature
 * Merfolk (Merrow Commerce) pays this too. Revealing leaves the card in hand (CR 701.20b), so the
 * Merfolk shown can still be cast the same turn.
 */
val SilvergillAdept = card("Silvergill Adept") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    power = 2
    toughness = 1
    oracleText = "As an additional cost to cast this spell, reveal a Merfolk card from your hand or " +
        "pay {3}.\nWhen this creature enters, draw a card."

    additionalCost(
        Costs.additional.RevealFromHandOrPay(
            filter = GameObjectFilter.Any.withSubtype(Subtype.MERFOLK),
            alternativeManaCost = "{3}"
        )
    )

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
        description = "When this creature enters, draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "86"
        artist = "Matt Cavotta"
        imageUri = "https://cards.scryfall.io/normal/front/9/e/9e07a38a-2d88-4b01-9634-f24cbc8d96d6.jpg?1783942897"
    }
}
