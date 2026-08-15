package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Rith's Attendant
 * {5}
 * Artifact Creature — Golem
 * 3/3
 * {1}, Sacrifice this creature: Add {R}{G}{W}.
 *
 * One of the Invasion "Attendant" cycle — each sacrifices for the three colors of the
 * matching dragon (Rith = Red/Green/White). A mana ability: no targets, no stack.
 */
val RithsAttendant = card("Rith's Attendant") {
    manaCost = "{5}"
    colorIdentity = "RGW"
    typeLine = "Artifact Creature — Golem"
    power = 3
    toughness = 3
    oracleText = "{1}, Sacrifice this creature: Add {R}{G}{W}."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.SacrificeSelf)
        effect = Effects.Composite(
            Effects.AddMana(Color.RED),
            Effects.AddMana(Color.GREEN),
            Effects.AddMana(Color.WHITE)
        )
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "310"
        artist = "Adam Rex"
        flavorText = "\"Rith is the claw of the ur-dragon, scattering seeds of devastation.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a26e8130-7fe9-4ef4-98af-928814f5b130.jpg?1562927816"
    }
}
