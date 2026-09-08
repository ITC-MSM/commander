package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Goldmeadow Stalwart
 * {W}
 * Creature — Kithkin Soldier
 * 2/2
 * As an additional cost to cast this spell, reveal a Kithkin card from your hand or pay {3}.
 *
 * The whole card is its cost — a 2/2 for {W} when the hand is Kithkin, a 2/2 for {3}{W} when it
 * isn't. Revealing keeps the card in hand (CR 701.20b), so one Kithkin can pay for several of
 * these across a turn.
 */
val GoldmeadowStalwart = card("Goldmeadow Stalwart") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin Soldier"
    power = 2
    toughness = 2
    oracleText = "As an additional cost to cast this spell, reveal a Kithkin card from your hand or pay {3}."

    additionalCost(
        Costs.additional.RevealFromHandOrPay(
            filter = GameObjectFilter.Any.withSubtype(Subtype.KITHKIN),
            alternativeManaCost = "{3}"
        )
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "18"
        artist = "Wayne Reynolds"
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6a7a9110-6aea-460b-91fa-5f8a507160e7.jpg?1783942915"
    }
}
