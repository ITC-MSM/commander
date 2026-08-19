package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Aegis of the Heavens
 * {1}{W}
 * Instant
 *
 * Target creature gets +1/+7 until end of turn.
 */
val AegisOfTheHeavens = card("Aegis of the Heavens") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gets +1/+7 until end of turn."

    spell {
        target = Targets.Creature
        effect = Effects.ModifyStats(1, 7, EffectTarget.ContextTarget(0))
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "1"
        artist = "Anthony Palumbo"
        flavorText = "Inner strength is never seen until it makes all the difference."
        imageUri = "https://cards.scryfall.io/normal/front/0/5/0503c55d-74bb-4165-9273-127c01bb2214.jpg?1783934612"
    }
}
