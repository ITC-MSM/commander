package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Abandoned Campground (DSK 255) — Duskmourn "horror" dual land cycle.
 *
 * Land
 * This land enters tapped unless a player has 13 or less life.
 * {T}: Add {W} or {U}.
 *
 * The enters-tapped clause checks *any* player's life (not just the controller), modeled with
 * [Conditions.APlayerLifeAtMost] (CR rules-faithful "a player has 13 or less life").
 */
val AbandonedCampground = card("Abandoned Campground") {
    typeLine = "Land"
    colorIdentity = "WU"
    oracleText = "This land enters tapped unless a player has 13 or less life.\n{T}: Add {W} or {U}."

    replacementEffect(EntersTapped(unlessCondition = Conditions.APlayerLifeAtMost(13)))

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.WHITE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLUE)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "255"
        artist = "Cristi Balanescu"
        flavorText = "They say every inhabitant vanished in a single night, leaving their belongings untouched but curiously covered in moths."
        imageUri = "https://cards.scryfall.io/normal/front/e/e/ee0565f5-ebdb-43f9-bbb4-0485b1968937.jpg?1726286826"
    }
}
