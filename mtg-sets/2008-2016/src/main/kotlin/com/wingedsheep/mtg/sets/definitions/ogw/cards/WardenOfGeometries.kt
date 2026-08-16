package com.wingedsheep.mtg.sets.definitions.ogw.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Warden of Geometries
 * {4}
 * Creature — Eldrazi Drone
 * 2/3
 * Vigilance
 * {T}: Add {C}.
 *
 * Vigilance is a printed keyword; the mana ability is the standard [Effects.AddColorlessMana]`(1)`
 * tap ability flagged `manaAbility = true` with [TimingRule.ManaAbility] so it never uses the stack.
 */
val WardenOfGeometries = card("Warden of Geometries") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Creature — Eldrazi Drone"
    power = 2
    toughness = 3
    oracleText = "Vigilance\n{T}: Add {C}. ({C} represents colorless mana.)"

    keywords(Keyword.VIGILANCE)

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
        description = "{T}: Add {C}."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "11"
        artist = "Jason Felix"
        flavorText = "The wastelands disorient even the most experienced scouts, making them easy prey for Kozilek's drones."
        imageUri = "https://cards.scryfall.io/normal/front/c/7/c7f517b9-ac10-4710-8ef1-ced1253d5ecf.jpg?1783937928"
    }
}
