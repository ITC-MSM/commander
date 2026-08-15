package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Soaring Stoneglider — Secrets of Strixhaven #32
 * {2}{W} · Creature — Elephant Cleric · 4/3
 *
 * As an additional cost to cast this spell, exile two cards from your graveyard or pay {1}{W}.
 * Flying, vigilance
 *
 * The "exile two cards from your graveyard or pay {1}{W}" additional cost is modeled with
 * [Costs.additional.ExileFromGraveyardOrPay]: the enumerator offers two cast paths — exile two
 * graveyard cards (base {2}{W}), or pay the alternative {1}{W} on top of the base cost.
 */
val SoaringStoneglider = card("Soaring Stoneglider") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Elephant Cleric"
    power = 4
    toughness = 3
    oracleText = "As an additional cost to cast this spell, exile two cards from your graveyard or pay {1}{W}.\n" +
        "Flying, vigilance"

    keywords(Keyword.FLYING, Keyword.VIGILANCE)

    additionalCost(
        Costs.additional.ExileFromGraveyardOrPay(
            exileCount = 2,
            alternativeManaCost = "{1}{W}",
        )
    )

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "32"
        artist = "Pauline Voss"
        flavorText = "\"Pranticle Peak is where you get the best views on land. I've found an even better vantage!\""
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2fbe446b-60fd-43da-8358-985392293af8.jpg?1775937136"
    }
}
