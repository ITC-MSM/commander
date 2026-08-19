package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats

/**
 * Drover of the Mighty
 * {1}{G}
 * Creature — Human Druid
 * 1/1
 *
 * This creature gets +2/+2 as long as you control a Dinosaur.
 * {T}: Add one mana of any color.
 */
val DroverOfTheMighty = card("Drover of the Mighty") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Druid"
    oracleText = "This creature gets +2/+2 as long as you control a Dinosaur.\n{T}: Add one mana of any color."
    power = 1
    toughness = 1

    staticAbility {
        ability = ModifyStats(2, 2, Filters.Self)
        condition = Conditions.ControlPermanentOfType(Subtype.DINOSAUR)
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana()
        manaAbility = true
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "187"
        artist = "Eric Deschamps"
        flavorText = "\"I do not lead. They do not follow. We walk together.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/1/51fdf2fc-d948-4c64-a34e-2f90eab83212.jpg?1783935726"
    }
}
