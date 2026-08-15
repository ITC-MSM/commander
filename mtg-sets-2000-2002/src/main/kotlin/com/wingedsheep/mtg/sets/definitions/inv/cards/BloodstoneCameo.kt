package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Bloodstone Cameo
 * {3}
 * Artifact
 *
 * {T}: Add {B} or {R}.
 */
val BloodstoneCameo = card("Bloodstone Cameo") {
    manaCost = "{3}"
    colorIdentity = "BR"
    typeLine = "Artifact"
    oracleText = "{T}: Add {B} or {R}."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
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
        collectorNumber = "298"
        artist = "Tony Szczudlo"
        flavorText = "\"The stone whispers to me of dragon's fire and darkness. I wish I'd never pried it from the figurehead of that sunken Keldon longship.\"\n—Isel, master carver"
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f9db32fa-64b2-4ef6-88f2-28e758d420bb.jpg?1562945391"
    }
}
