package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Troll-Horn Cameo
 * {3}
 * Artifact
 *
 * {T}: Add {R} or {G}.
 */
val TrollHornCameo = card("Troll-Horn Cameo") {
    manaCost = "{3}"
    colorIdentity = "RG"
    typeLine = "Artifact"
    oracleText = "{T}: Add {R} or {G}."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "316"
        artist = "Donato Giancola"
        flavorText = "\"I found a troll-horn fragment in the wooded foothills of Hurloon, and it keeps growing larger. I wonder, is the horn recreating itself or the troll?\"\n—Isel, master carver"
        imageUri = "https://cards.scryfall.io/normal/front/4/2/42b1ca6c-6ca0-4b02-885a-58cee3fa2aa8.jpg?1562908389"
    }
}
