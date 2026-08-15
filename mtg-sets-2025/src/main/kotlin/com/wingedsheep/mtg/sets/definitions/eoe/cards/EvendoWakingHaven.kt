package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.station

/**
 * Evendo, Waking Haven
 * Land — Planet
 * This land enters tapped.
 * {T}: Add {G}.
 * Station (Tap another creature you control: Put charge counters equal to its power on this Planet. Station only as a sorcery.)
 * 12+ | {G}, {T}: Add {G} for each creature you control.
 */
val EvendoWakingHaven = card("Evendo, Waking Haven") {
    typeLine = "Land — Planet"
    colorIdentity = "G"
    oracleText = "This land enters tapped.\n{T}: Add {G}.\nStation (Tap another creature you control: Put charge counters equal to its power on this Planet. Station only as a sorcery.)\n12+ | {G}, {T}: Add {G} for each creature you control."

    // This land enters tapped
    replacementEffect(EntersTapped())

    // Basic mana ability: {T}: Add {G}
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
    }

    // Station activated ability: tap another creature → add charge counters equal to its power
    station()

    // Conditional mana ability: {G}, {T}: Add {G} for each creature you control at 12+ charge counters
    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{G}"),
            Costs.Tap
        )
        effect = Effects.AddMana(Color.GREEN, DynamicAmounts.creaturesYouControl())
        restrictions = listOf(
            ActivationRestriction.OnlyIfCondition(
                Conditions.SourceCounterCountAtLeast(Counters.CHARGE, 12)
            )
        )
        manaAbility = true
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "253"
        artist = "Adam Paquette"
        imageUri = "https://cards.scryfall.io/normal/front/2/f/2fa09104-acbe-4410-b101-2fe6ac28efde.jpg?1755341333"
    }
}
