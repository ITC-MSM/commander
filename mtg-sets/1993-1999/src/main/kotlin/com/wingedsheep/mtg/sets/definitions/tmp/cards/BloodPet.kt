package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Blood Pet
 * {B}
 * Creature — Thrull
 * 1/1
 * Sacrifice this creature: Add {B}.
 */
val BloodPet = card("Blood Pet") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Creature — Thrull"
    power = 1
    toughness = 1
    oracleText = "Sacrifice this creature: Add {B}."

    activatedAbility {
        cost = Costs.SacrificeSelf
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "109"
        artist = "Brom"
        flavorText = "\"You are wrong,\" Volrath said. \"I do not hate the living. They often prove quite useful to me.\" And then he laughed."
        imageUri = "https://cards.scryfall.io/normal/front/5/a/5a89ba1b-e68b-4d70-a25e-27be9bf48a3b.jpg"
    }
}
