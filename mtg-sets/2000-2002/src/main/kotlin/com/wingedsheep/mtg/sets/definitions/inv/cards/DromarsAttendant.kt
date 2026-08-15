package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Dromar's Attendant
 * {5}
 * Artifact Creature — Golem
 * 3/3
 * {1}, Sacrifice this creature: Add {W}{U}{B}.
 *
 * One of the Invasion "Attendant" cycle (cf. [RithsAttendant]) — each sacrifices for the three
 * colors of the matching dragon (Dromar = White/Blue/Black). A mana ability: no targets, no stack.
 */
val DromarsAttendant = card("Dromar's Attendant") {
    manaCost = "{5}"
    colorIdentity = "WUB"
    typeLine = "Artifact Creature — Golem"
    power = 3
    toughness = 3
    oracleText = "{1}, Sacrifice this creature: Add {W}{U}{B}."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeSelf)
        effect = Effects.Composite(
            Effects.AddMana(Color.WHITE),
            Effects.AddMana(Color.BLUE),
            Effects.AddMana(Color.BLACK)
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "303"
        artist = "Carl Critchlow"
        imageUri = "https://cards.scryfall.io/normal/front/2/4/24936fa9-41a3-4da5-91cf-c28fa45f47c9.jpg?1562902350"
    }
}
