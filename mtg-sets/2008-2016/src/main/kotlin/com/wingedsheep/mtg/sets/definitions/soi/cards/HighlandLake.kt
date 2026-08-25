package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Highland Lake
 * Land
 * This land enters tapped.
 * {T}: Add {U} or {R}.
 *
 * Highland Lake has no basic land types, so the dual mana ability is spelled out as two
 * independent [TimingRule.ManaAbility] activations rather than being intrinsic to the type line.
 */
val HighlandLake = card("Highland Lake") {
    colorIdentity = "RU"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {U} or {R}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "277"
        artist = "Florian de Gesincourt"
        flavorText = "With the fate of Innistrad uncertain, some seek solace in remote areas."
        imageUri = "https://cards.scryfall.io/normal/front/4/8/48b97a23-c4cf-47c9-9dbf-34215ea0e908.jpg"
    }
}
