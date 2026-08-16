package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Demon's Grasp
 * {4}{B}
 * Sorcery
 * Target creature gets -5/-5 until end of turn.
 */
val DemonsGrasp = card("Demon's Grasp") {
    manaCost = "{4}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Target creature gets -5/-5 until end of turn."

    spell {
        val t = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(-5, -5, t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "108"
        artist = "David Gaillet"
        flavorText = "\"Take what solace you can in the knowledge that you will not be here to witness Zendikar's demise.\" —Ob Nixilis"
        imageUri = "https://cards.scryfall.io/normal/front/f/f/ff953948-db26-43af-9905-7d387769b4a9.jpg?1783938202"
    }
}
