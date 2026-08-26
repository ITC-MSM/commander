package com.wingedsheep.mtg.sets.definitions.ths.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Vanquish the Foul
 * {5}{W}
 * Sorcery
 *
 * Destroy target creature with power 4 or greater. Scry 1. (Look at the top card of your library. You may put that card on the bottom.)
 */
val VanquishTheFoul = card("Vanquish the Foul") {
    manaCost = "{5}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Destroy target creature with power 4 or greater. Scry 1. (Look at the top card of your library. You may put that card on the bottom.)"

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.powerAtLeast(4)))
        effect = Effects.Composite(
            Effects.Move(t, Zone.GRAVEYARD, byDestruction = true),
            Effects.Scry(1)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "35"
        artist = "Eric Deschamps"
        imageUri = "https://cards.scryfall.io/normal/front/8/f/8fdcec06-e33c-4737-b81e-b156d6e3fd77.jpg"
    }
}
