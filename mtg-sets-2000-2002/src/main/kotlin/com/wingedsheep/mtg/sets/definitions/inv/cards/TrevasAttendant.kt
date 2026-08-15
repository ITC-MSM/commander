package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Treva's Attendant
 * {5}
 * Artifact Creature — Golem
 * 3/3
 * {1}, Sacrifice this creature: Add {G}{W}{U}.
 *
 * One of the Invasion "Attendant" cycle — each sacrifices for the three colors of the
 * matching dragon (Treva = Green/White/Blue). A mana ability: no targets, no stack.
 */
val TrevasAttendant = card("Treva's Attendant") {
    manaCost = "{5}"
    colorIdentity = "GWU"
    typeLine = "Artifact Creature — Golem"
    power = 3
    toughness = 3
    oracleText = "{1}, Sacrifice this creature: Add {G}{W}{U}."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeSelf)
        effect = Effects.Composite(
            Effects.AddMana(Color.GREEN),
            Effects.AddMana(Color.WHITE),
            Effects.AddMana(Color.BLUE)
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "315"
        artist = "Christopher Moeller"
        imageUri = "https://cards.scryfall.io/normal/front/9/8/9857af81-fb95-4dc4-b048-9ce4e96d1eca.jpg?1562925750"
    }
}
