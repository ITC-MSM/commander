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
 * Strangled Cemetery (DSK 268) — Duskmourn "horror" dual land cycle.
 *
 * Land
 * This land enters tapped unless a player has 13 or less life.
 * {T}: Add {B} or {G}.
 *
 * The enters-tapped clause checks *any* player's life (not just the controller), modeled with
 * [Conditions.APlayerLifeAtMost] ("a player has 13 or less life").
 */
val StrangledCemetery = card("Strangled Cemetery") {
    typeLine = "Land"
    colorIdentity = "BG"
    oracleText = "This land enters tapped unless a player has 13 or less life.\n{T}: Add {B} or {G}."

    replacementEffect(EntersTapped(unlessCondition = Conditions.APlayerLifeAtMost(13)))

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.BLACK)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.GREEN)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "268"
        artist = "Marco Gorlei"
        flavorText = "They say that with each new moon, the buried dead rise and dance from dusk to dawn, and witnesses to the revelry must join them for eternity."
        imageUri = "https://cards.scryfall.io/normal/front/c/1/c1ce9250-bdbe-4c77-9243-6db9ffffe69b.jpg?1726286874"
    }
}
