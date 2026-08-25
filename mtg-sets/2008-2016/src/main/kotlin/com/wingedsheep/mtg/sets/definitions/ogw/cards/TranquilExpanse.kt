package com.wingedsheep.mtg.sets.definitions.ogw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Tranquil Expanse
 * Land
 * This land enters tapped.
 * {T}: Add {G} or {W}.
 */
val TranquilExpanse = card("Tranquil Expanse") {
    colorIdentity = "GW"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {G} or {W}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "180"
        artist = "Cliff Childs"
        flavorText = "Despite the chaos of the Roil and the devastation of the Eldrazi, Zendikar is a world of breathtaking beauty."
        imageUri = "https://cards.scryfall.io/normal/front/4/2/42787d2b-3f9d-4c29-9ece-e65544eb1340.jpg"
    }
}
