package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Prismatic Lens
 * {2}
 * Artifact
 * {T}: Add {C}.
 * {1}, {T}: Add one mana of any color.
 */
val PrismaticLens = card("Prismatic Lens") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{T}: Add {C}.\n" +
        "{1}, {T}: Add one mana of any color."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        effect = Effects.AddManaOfChoice()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "262"
        artist = "Alan Pollack"
        flavorText = "It bends not light but mana, aligning its chaotic currents into the sharp angles necessary for the mystic's purposes."
        imageUri = "https://cards.scryfall.io/normal/front/5/0/50f058a7-c3c7-4bdf-a66c-a2636a8bd9db.jpg"
    }
}
