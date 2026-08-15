package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Darigaaz's Attendant
 * {5}
 * Artifact Creature — Golem
 * 3/3
 * {1}, Sacrifice this creature: Add {B}{R}{G}.
 *
 * One of the Invasion "Attendant" cycle (cf. [RithsAttendant]) — each sacrifices for the three
 * colors of the matching dragon (Darigaaz = Black/Red/Green). A mana ability: no targets, no stack.
 */
val DarigaazsAttendant = card("Darigaaz's Attendant") {
    manaCost = "{5}"
    colorIdentity = "BRG"
    typeLine = "Artifact Creature — Golem"
    power = 3
    toughness = 3
    oracleText = "{1}, Sacrifice this creature: Add {B}{R}{G}."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeSelf)
        effect = Effects.Composite(
            Effects.AddMana(Color.BLACK),
            Effects.AddMana(Color.RED),
            Effects.AddMana(Color.GREEN)
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "301"
        artist = "Brom"
        imageUri = "https://cards.scryfall.io/normal/front/6/f/6f22b575-443a-4c06-8e75-d4140cbd3660.jpg?1562917206"
    }
}
