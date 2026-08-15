package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Savage Mansion
 * Land
 * This land enters tapped.
 * {T}: Add {R} or {G}.
 * {4}, {T}: Surveil 1. (Look at the top card of your library. You may put it into your graveyard.)
 */
val SavageMansion = card("Savage Mansion") {
    manaCost = ""
    colorIdentity = "RG"
    typeLine = "Land"
    oracleText = "This land enters tapped.\n{T}: Add {R} or {G}.\n{4}, {T}: Surveil 1. (Look at the top card of your library. You may put it into your graveyard.)"
    replacementEffect(EntersTapped())
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}"), Costs.Tap)
        effect = Patterns.Library.surveil(1)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "183"
        artist = "David Álvarez"
        flavorText = "Exotic trophies from victorious hunts adorn the walls of the Kravinoff estate."
        imageUri = "https://cards.scryfall.io/normal/front/8/5/855f59a5-17a8-4aca-8a4d-f98111eba14c.jpg?1757378170"
    }
}
