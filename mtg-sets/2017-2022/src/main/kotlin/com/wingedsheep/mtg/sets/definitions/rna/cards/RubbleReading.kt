package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Rubble Reading — Ravnica Allegiance #110
 * {3}{R} · Sorcery
 *
 * Land destruction with a scry 2 rider — the same destroy-then-scry composite as
 * [GetThePoint].
 */
val RubbleReading = card("Rubble Reading") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Destroy target land. Scry 2."

    spell {
        val land = target("target", Targets.Land)
        effect = Effects.Composite(listOf(
            Effects.Destroy(land),
            Effects.Scry(2)
        ))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "110"
        artist = "Aaron Miller"
        flavorText = "Gruul oracles see omens in all forms of destruction: the entrails of a maaka's prey, the flight of vultures over a battlefield, the scattering of toppled stone."
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8ace095a-3ea9-4121-8ffb-5b3612b96985.jpg"
    }
}
