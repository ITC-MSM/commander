package com.wingedsheep.mtg.sets.definitions.clb.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Bronze Walrus
 * {3}
 * Artifact Creature — Walrus
 * 2/2
 * When this creature enters, scry 2. (Look at the top two cards of your library, then put any number of them on the bottom and the rest on top in any order.)
 * {T}: Add one mana of any color.
 *
 * Two stock bodies side by side: [Triggers.EntersBattlefield] with the [Effects.Scry] macro, and the
 * any-color mana ability, which needs `manaAbility`/[TimingRule.ManaAbility] so it resolves without the stack.
 */
val BronzeWalrus = card("Bronze Walrus") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Walrus"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, scry 2. (Look at the top two cards of your library, then put any number of them on the bottom and the rest on top in any order.)\n" +
        "{T}: Add one mana of any color."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Scry(2)
        description = "When this creature enters, scry 2."
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "302"
        artist = "James Paick"
        imageUri = "https://cards.scryfall.io/normal/front/b/2/b2cfd2c0-2110-47f1-809e-487b9b0a1043.jpg?1783922682"
    }
}
