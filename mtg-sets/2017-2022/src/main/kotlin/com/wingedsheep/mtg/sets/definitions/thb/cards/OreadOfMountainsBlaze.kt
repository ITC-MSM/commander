package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Oread of Mountain's Blaze
 * {1}{R}
 * Enchantment Creature — Nymph
 * 1/3
 *
 * {2}{R}, Discard a card: Draw a card.
 *
 * A plain looter on a stick: the discard is an unfiltered [Costs.DiscardCard] cost atom, so the card
 * leaves the hand as the ability is activated, before the replacement draw ever happens.
 */
val OreadOfMountainsBlaze = card("Oread of Mountain's Blaze") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment Creature — Nymph"
    power = 1
    toughness = 3
    oracleText = "{2}{R}, Discard a card: Draw a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}{R}"), Costs.DiscardCard)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "146"
        artist = "Yigit Koroglu"
        flavorText = "\"Flame-wrapped, she dances a burning swath amid the clouds.\"\n—Psemilla, Meletian poet"
        imageUri = "https://cards.scryfall.io/normal/front/0/f/0f9bb73a-5b4e-4c9b-b0a6-8e531dc27394.jpg"
    }
}
