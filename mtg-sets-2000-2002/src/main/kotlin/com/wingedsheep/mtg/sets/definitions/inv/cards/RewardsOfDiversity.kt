package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Rewards of Diversity
 * {2}{W}
 * Enchantment
 * Whenever an opponent casts a multicolored spell, you gain 4 life.
 */
val RewardsOfDiversity = card("Rewards of Diversity") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Whenever an opponent casts a multicolored spell, you gain 4 life."

    triggeredAbility {
        trigger = Triggers.opponentCasts(GameObjectFilter.Multicolored)
        effect = Effects.GainLife(4)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "32"
        artist = "Darrell Riche"
        flavorText = "\"Everything is in place. Nothing can happen that isn't part of my plan.\"\n—Urza"
        imageUri = "https://cards.scryfall.io/normal/front/0/4/04116b38-8fb1-47c6-b68d-060d0fc4a60d.jpg?1562895781"
    }
}
