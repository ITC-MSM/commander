package com.wingedsheep.mtg.sets.definitions.cmd.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Command Tower
 * Land
 *
 * {T}: Add one mana of any color in your commander's color identity.
 */
val CommandTower = card("Command Tower") {
    typeLine = "Land"
    colorIdentity = ""
    oracleText = "{T}: Add one mana of any color in your commander's color identity."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddManaOfColorInCommanderColorIdentity()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "269"
        artist = "Ryan Yee"
        flavorText = "When defeat is near and guidance is scarce, all eyes look in one direction."
        imageUri = "https://cards.scryfall.io/normal/front/4/6/46982091-cc78-4171-8b3d-d07592684728.jpg?1592714567"
        ruling("2020-11-10", "If your commander is a card that has no colors in its color identity, Command Tower's ability produces no mana. It doesn't produce {C}.")
        ruling("2020-11-10", "If you have two commanders, the ability adds one mana of any color in their combined color identities.")
        ruling("2020-11-10", "If you don't have a commander, Command Tower's ability produces no mana.")
    }
}
