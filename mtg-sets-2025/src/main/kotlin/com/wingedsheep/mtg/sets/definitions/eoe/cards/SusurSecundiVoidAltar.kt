package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.station

/**
 * Susur Secundi, Void Altar
 * Land — Planet
 * This land enters tapped.
 * {T}: Add {B}.
 * Station (Tap another creature you control: Put charge counters equal to its power on this Planet. Station only as a sorcery.)
 * 12+ | {1}{B}, {T}, Pay 2 life, Sacrifice a creature: Draw cards equal to the sacrificed creature's power. Activate only as a sorcery.
 */
val SusurSecundiVoidAltar = card("Susur Secundi, Void Altar") {
    typeLine = "Land — Planet"
    colorIdentity = "B"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {B}.\n" +
        "Station (Tap another creature you control: Put charge counters equal to its power on this Planet. Station only as a sorcery.)\n" +
        "12+ | {1}{B}, {T}, Pay 2 life, Sacrifice a creature: Draw cards equal to the sacrificed creature's power. Activate only as a sorcery."

    // This land enters tapped
    replacementEffect(EntersTapped())

    // Basic mana ability: {T}: Add {B}
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
    }

    // Station: tap another creature → add charge counters equal to its power
    station()

    // 12+ charge counters: {1}{B}, {T}, Pay 2 life, Sacrifice a creature:
    // Draw cards equal to the sacrificed creature's power
    val charge12 = Conditions.SourceCounterCountAtLeast(Counters.CHARGE, 12)

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}{B}"),
            Costs.Tap,
            Costs.PayLife(2),
            Costs.Sacrifice(GameObjectFilter.Creature)
        )
        effect = Effects.DrawCards(DynamicAmounts.sacrificedPower())
        timing = TimingRule.SorcerySpeed
        restrictions = listOf(ActivationRestriction.OnlyIfCondition(charge12))
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "259"
        artist = "Adam Paquette"
        imageUri = "https://cards.scryfall.io/normal/front/a/e/aefb8c0d-2bc6-4bec-851e-0137b4abfb22.jpg?1755341450"
        ruling("2025-07-25", "A station card is a card with the station keyword ability. The station keyword means \"Tap another untapped creature you control: Put a number of charge counters on this permanent equal to the tapped creature's power. Activate only as a sorcery.\"")
        ruling("2025-07-25", "Each station symbol represents an ability. A station symbol means \"As long as this permanent has N or more charge counters on it, it has [abilities]\".")
        ruling("2025-07-25", "Planet is a land subtype with no special meaning. It doesn't grant the land any intrinsic abilities.")
    }
}
