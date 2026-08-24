package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Warmth
 * {1}{W}
 * Enchantment
 * Whenever an opponent casts a red spell, you gain 2 life.
 */
val Warmth = card("Warmth") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Enchantment"
    oracleText = "Whenever an opponent casts a red spell, you gain 2 life."

    triggeredAbility {
        trigger = Triggers.opponentCasts(GameObjectFilter.Any.withColor(Color.RED))
        effect = Effects.GainLife(2)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "51"
        artist = "Drew Tucker"
        flavorText = "\"Flame grows gentle with but a little distance.\"\n" +
            "—Orim, Samite healer"
        imageUri = "https://cards.scryfall.io/normal/front/d/7/d7dbeea8-06b0-4482-bdae-aa82b9db8856.jpg"
    }
}
