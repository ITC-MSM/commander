package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Stormbind
 * {1}{R}{G}
 * Enchantment
 *
 * {2}, Discard a card at random: This enchantment deals 2 damage to any target.
 *
 * The Kris Mage shape on an enchantment: a `Costs.Composite` of the mana and the discard atom, with
 * `atRandom = true` so the engine picks the card and the cost takes no player selection.
 */
val Stormbind = card("Stormbind") {
    manaCost = "{1}{R}{G}"
    colorIdentity = "GR"
    typeLine = "Enchantment"
    oracleText = "{2}, Discard a card at random: This enchantment deals 2 damage to any target."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Discard(atRandom = true))
        val t = target("target", Targets.Any)
        effect = Effects.DealDamage(2, t)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "304"
        artist = "NéNé Thomas & Phillip Mosness"
        flavorText = "\"Once, our people could call down the storm itself to do our bidding.\"\n—Lovisa Coldeyes, Balduvian Chieftain"
        imageUri = "https://cards.scryfall.io/normal/front/c/2/c2d5d91b-aeb4-4d7e-b748-77f9960da55f.jpg"
    }
}
