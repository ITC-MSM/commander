package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ForceSacrificeEffect

/**
 * Diabolic Edict
 * {1}{B}
 * Instant
 * Target player sacrifices a creature of their choice.
 */
val DiabolicEdict = card("Diabolic Edict") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target player sacrifices a creature of their choice."

    spell {
        val player = target("target player", Targets.Player)
        effect = ForceSacrificeEffect(GameObjectFilter.Creature, 1, player)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "128"
        artist = "Ron Spencer"
        flavorText = "Greven il-Vec lifted Vhati off his feet. \"The fall will give you time to think on your failure.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/2/a2ecf2ee-1e2d-4ab2-8b2c-717c794b09b2.jpg?1562055897"
    }
}
