package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Foul Orchard
 * Land
 * This land enters tapped.
 * {T}: Add {B} or {G}.
 */
val FoulOrchard = card("Foul Orchard") {
    colorIdentity = "BG"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {B} or {G}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
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
        collectorNumber = "275"
        artist = "Jung Park"
        flavorText = "\"Such a beautiful place for a stroll.\"\n" +
            "—Liliana Vess"
        imageUri = "https://cards.scryfall.io/normal/front/2/3/2378ac21-9912-40d7-972d-b1b71a8e4984.jpg"
    }
}
