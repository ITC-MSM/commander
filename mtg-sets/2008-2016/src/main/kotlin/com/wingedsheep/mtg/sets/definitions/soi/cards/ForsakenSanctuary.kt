package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Forsaken Sanctuary
 * Land
 * This land enters tapped.
 * {T}: Add {W} or {B}.
 */
val ForsakenSanctuary = card("Forsaken Sanctuary") {
    colorIdentity = "BW"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {W} or {B}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
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
        collectorNumber = "273"
        artist = "Vincent Proce"
        flavorText = "\"Prayers will curdle on the tongue and be heard by rotting ears.\"\n" +
            "—Minaldra, the Vizag Atum"
        imageUri = "https://cards.scryfall.io/normal/front/0/b/0bd86cac-08c1-4db0-ab54-4bb65a771efe.jpg"
    }
}
