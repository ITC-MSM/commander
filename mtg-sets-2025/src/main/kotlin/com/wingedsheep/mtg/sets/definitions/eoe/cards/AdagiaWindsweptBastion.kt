package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Supertype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.station

/**
 * Adagia, Windswept Bastion
 * Land — Planet
 * This land enters tapped.
 * {T}: Add {W}.
 * Station (Tap another creature you control: Put charge counters equal to its power on this Planet. Station only as a sorcery.)
 * 12+ | {3}{W}, {T}: Create a token that's a copy of target artifact or enchantment you control, except it's legendary. Activate only as a sorcery.
 */
val AdagiaWindsweptBastion = card("Adagia, Windswept Bastion") {
    typeLine = "Land — Planet"
    colorIdentity = "W"
    oracleText = "This land enters tapped.\n" +
        "{T}: Add {W}.\n" +
        "Station (Tap another creature you control: Put charge counters equal to its power on this Planet. Station only as a sorcery.)\n" +
        "12+ | {3}{W}, {T}: Create a token that's a copy of target artifact or enchantment you control, except it's legendary. Activate only as a sorcery."

    // This land enters tapped
    replacementEffect(EntersTapped())

    // Basic mana ability: {T}: Add {W}
    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
    }

    // Station: tap another creature → add charge counters equal to its power
    station()

    // 12+ charge counters: {3}{W}, {T}: Create a legendary token copy of target artifact or enchantment you control
    val charge12 = Conditions.SourceCounterCountAtLeast(Counters.CHARGE, 12)

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{3}{W}"),
            Costs.Tap
        )
        val targetPermanent = target(
            "target artifact or enchantment you control",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.ArtifactOrEnchantment.youControl()))
        )
        effect = Effects.CreateTokenCopyOfTarget(
            target = targetPermanent,
            addedSupertypes = setOf(Supertype.LEGENDARY)
        )
        timing = TimingRule.SorcerySpeed
        restrictions = listOf(ActivationRestriction.OnlyIfCondition(charge12))
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "250"
        artist = "Adam Paquette"
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c634273a-94b0-4104-9d10-ae522ece1fc7.jpg?1755341328"
        ruling("2025-07-25", "A station card is a card with the station keyword ability. The station keyword means \"Tap another untapped creature you control: Put a number of charge counters on this permanent equal to the tapped creature's power. Activate only as a sorcery.\"")
        ruling("2025-07-25", "Each station symbol represents an ability. A station symbol means \"As long as this permanent has N or more charge counters on it, it has [abilities]\".")
        ruling("2025-07-25", "If Adagia's last ability creates a token copy of a nonlegendary permanent, you'll control two permanents with the same name, but only one of them (the newly created token) will be legendary. You won't put any of them into their owners' graveyards.")
        ruling("2025-07-25", "The token created by Adagia's last ability copies exactly what was printed on the original permanent and nothing else, with the listed exception (unless that permanent is copying something else or is a token).")
        ruling("2025-07-25", "Planet is a land subtype with no special meaning. It doesn't grant the land any intrinsic abilities.")
    }
}
