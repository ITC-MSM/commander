package com.wingedsheep.mtg.sets.definitions.ogw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped

/**
 * Cinder Barrens
 * Land
 * This land enters tapped.
 * {T}: Add {B} or {R}.
 */
val CinderBarrens = card("Cinder Barrens") {
    colorIdentity = "BR"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {B} or {R}."

    replacementEffect(EntersTapped())

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "168"
        artist = "Cliff Childs"
        flavorText = "A mudflow swallowed the lowlands years ago. All that remains are a bottomless mire and an endless rain of ash."
        imageUri = "https://cards.scryfall.io/normal/front/9/4/949f41a0-9082-4682-88b8-a29fbcb48d5d.jpg"
    }
}
