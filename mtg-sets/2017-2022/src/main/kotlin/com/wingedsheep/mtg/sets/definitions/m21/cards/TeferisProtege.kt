package com.wingedsheep.mtg.sets.definitions.m21.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Teferi's Protege
 * {2}{U}
 * Creature — Human Wizard
 * 2/3
 * {1}{U}, {T}: Draw a card, then discard a card.
 *
 * The looter shape (Qiqirn Merchant): a single composite effect so the draw and the discard both
 * happen while the ability resolves, with no player action between them.
 */
val TeferisProtege = card("Teferi's Protege") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 3
    oracleText = "{1}{U}, {T}: Draw a card, then discard a card."

    // {1}{U}, {T}: Draw a card, then discard a card.
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{U}"), Costs.Tap)
        effect = Effects.Composite(
            Effects.DrawCards(1),
            Patterns.Hand.discardCards(1),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "77"
        artist = "Bram Sels"
        flavorText = "Teferi's legacy lives on in Tolaria through the study of time magic and a tradition of irrepressible mischief."
        imageUri = "https://cards.scryfall.io/normal/front/5/4/5449d71c-5c1b-44c6-9407-0212aa3c3e3a.jpg?1783930718"
        ruling("2020-06-23", "You draw a card and discard a card all while the ability is resolving. Nothing can happen between the two, and no player may choose to take actions.")
    }
}
