package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Strangling Spores
 * {3}{B}
 * Instant
 * Target creature gets -3/-3 until end of turn.
 */
val StranglingSpores = card("Strangling Spores") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets -3/-3 until end of turn."

    spell {
        val creature = target("target creature", TargetCreature())
        effect = Effects.ModifyStats(power = -3, toughness = -3, target = creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "122"
        artist = "Jason A. Engle"
        flavorText = "Imagine a thousand tiny mushrooms cropping up within your lungs."
        imageUri = "https://cards.scryfall.io/normal/front/3/0/300468ab-fbae-42ae-97bc-b08f795efa5c.jpg?1783934561"
    }
}
