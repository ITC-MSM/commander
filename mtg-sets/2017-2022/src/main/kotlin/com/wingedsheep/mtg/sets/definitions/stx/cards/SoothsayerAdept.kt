package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Soothsayer Adept — Strixhaven: School of Mages #55 (canonical printing)
 * {1}{U} · Creature — Merfolk Wizard · 1/3
 *
 * {1}{U}, {T}: Draw a card, then discard a card.
 *
 * The Merfolk Looter loot with a mana rider: [Costs.Composite] of the mana and the tap, and the
 * effect is [Effects.DrawCards] `then` [Patterns.Hand.discardCards] — the discard is the standard
 * Gather → Select → Move pipeline over your hand, so the freshly drawn card is itself a legal
 * discard.
 */
val SoothsayerAdept = card("Soothsayer Adept") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    oracleText =
        "{1}{U}, {T}: Draw a card, then discard a card."
    power = 1
    toughness = 3

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{U}"), Costs.Tap)
        effect = Effects.DrawCards(1) then Patterns.Hand.discardCards(1)
        description = "{1}{U}, {T}: Draw a card, then discard a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "55"
        artist = "Cristi Balanescu"
        flavorText = "One of the first lessons divination students learn is how to recognize the difference between a portent and a simple surface ripple."
        imageUri = "https://cards.scryfall.io/normal/front/e/b/eb93c9d2-88eb-403d-bb67-8e8b0462ac27.jpg?1783927374"
    }
}
