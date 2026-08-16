package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Reave Soul
 * {1}{B}
 * Sorcery
 * Destroy target creature with power 3 or less.
 */
val ReaveSoul = card("Reave Soul") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Destroy target creature with power 3 or less."

    spell {
        val creature = target("target creature with power 3 or less", Targets.CreatureWithPowerAtMost(3))
        effect = Effects.Destroy(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "115"
        artist = "David Palumbo"
        flavorText = "\"I was not convinced you had a soul until I saw it for myself.\" —Liliana Vess"
        imageUri = "https://cards.scryfall.io/normal/front/d/b/db3d5e9d-07e8-43e1-aaf0-1f9e4ed2834a.jpg?1783938337"
    }
}
