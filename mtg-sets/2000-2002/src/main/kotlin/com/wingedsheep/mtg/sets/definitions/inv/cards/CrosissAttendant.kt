package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Crosis's Attendant
 * {5}
 * Artifact Creature — Golem
 * 3/3
 * {1}, Sacrifice this creature: Add {U}{B}{R}.
 *
 * One of the Invasion "Attendant" cycle (cf. [RithsAttendant]) — each sacrifices for the three
 * colors of the matching dragon (Crosis = Blue/Black/Red). A mana ability: no targets, no stack.
 */
val CrosissAttendant = card("Crosis's Attendant") {
    manaCost = "{5}"
    colorIdentity = "UBR"
    typeLine = "Artifact Creature — Golem"
    power = 3
    toughness = 3
    oracleText = "{1}, Sacrifice this creature: Add {U}{B}{R}."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeSelf)
        effect = Effects.Composite(
            Effects.AddMana(Color.BLUE),
            Effects.AddMana(Color.BLACK),
            Effects.AddMana(Color.RED)
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "300"
        artist = "Arnie Swekel"
        imageUri = "https://cards.scryfall.io/normal/front/4/5/45edc18c-2046-4d0e-92fe-a6cf4aaf1c6f.jpg?1562909185"
    }
}
