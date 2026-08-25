package com.wingedsheep.mtg.sets.definitions.xln.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Blossom Dryad
 * {2}{G}
 * Creature — Dryad
 * 2/2
 *
 * {T}: Untap target land.
 */
val BlossomDryad = card("Blossom Dryad") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Dryad"
    oracleText = "{T}: Untap target land."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Tap
        val land = target("target", Targets.Land)
        effect = Effects.Untap(land)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "178"
        artist = "Shreya Shetty"
        flavorText = "The only force on Ixalan not interested in finding the golden city is Ixalan itself."
        imageUri = "https://cards.scryfall.io/normal/front/6/6/66a3645f-2b71-4816-a1f2-6cd4e987882f.jpg"
    }
}
