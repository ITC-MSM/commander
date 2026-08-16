package com.wingedsheep.mtg.sets.definitions.dmu.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Tattered Apparition
 * {3}{B}
 * Creature — Shade
 * 2/2
 * Flying
 * {1}{B}: This creature gets +1/+1 until end of turn.
 */
val TatteredApparition = card("Tattered Apparition") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Shade"
    oracleText = "Flying\n{1}{B}: This creature gets +1/+1 until end of turn."
    power = 2
    toughness = 2

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{1}{B}")
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "111"
        artist = "Jason A. Engle"
        flavorText = "\"Ah yes, I remember that one. I turned him inside out and cast his soul into an endless nightmare a few centuries ago.\"\n—Braids"
        imageUri = "https://cards.scryfall.io/normal/front/0/3/0368e91c-31ee-4b81-a361-30a4555b1a42.jpg?1783921324"
    }
}
