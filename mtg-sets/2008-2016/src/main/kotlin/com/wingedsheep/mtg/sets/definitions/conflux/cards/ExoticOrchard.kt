package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.values.LandControllerScope

/**
 * Exotic Orchard
 * Land
 *
 * {T}: Add one mana of any color that a land an opponent controls could produce.
 */
val ExoticOrchard = card("Exotic Orchard") {
    typeLine = "Land"
    colorIdentity = ""
    oracleText = "{T}: Add one mana of any color that a land an opponent controls could produce."

    activatedAbility {
        cost = AbilityCost.Tap
        effect = Effects.AddManaOfColorLandsCouldProduce(LandControllerScope.OPPONENTS)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "142"
        artist = "Steven Belledin"
        flavorText = "\"It was a strange morning. When we awoke, we found our trees transformed. We didn't know whether to water them or polish them.\"\n—Pulan, Bant orchardist"
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6aae6480-4e71-4d94-a648-f80d3849d792.jpg?1562801539"
    }
}
