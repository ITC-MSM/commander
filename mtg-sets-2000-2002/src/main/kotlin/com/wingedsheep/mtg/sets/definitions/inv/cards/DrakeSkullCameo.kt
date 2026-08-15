package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Drake-Skull Cameo
 * {3}
 * Artifact
 *
 * {T}: Add {U} or {B}.
 */
val DrakeSkullCameo = card("Drake-Skull Cameo") {
    manaCost = "{3}"
    colorIdentity = "UB"
    typeLine = "Artifact"
    oracleText = "{T}: Add {U} or {B}."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "302"
        artist = "Dan Frazier"
        flavorText = "\"A strange skull was turned up by an Ephran farmer's plow. I traded a copper ring for the 'ox skull.' It resonates of the sea and danger.\"\n—Isel, master carver"
        imageUri = "https://cards.scryfall.io/normal/front/4/a/4a3ce135-9c2f-45bd-b2db-c0e00c50c964.jpg?1562909996"
    }
}
