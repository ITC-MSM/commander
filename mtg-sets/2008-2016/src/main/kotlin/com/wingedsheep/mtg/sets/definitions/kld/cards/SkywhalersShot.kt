package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Skywhaler's Shot
 * {2}{W}
 * Instant
 *
 * Destroy target creature with power 3 or greater. Scry 1. (Look at the top card of your library. You may put that card on the bottom.)
 *
 * "Power 3 or greater" is a targeting restriction, not a resolution check, so it rides the target
 * filter — a creature that shrinks below 3 power in response becomes an illegal target and the
 * spell fizzles, taking the scry with it.
 */
val SkywhalersShot = card("Skywhaler's Shot") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Destroy target creature with power 3 or greater. Scry 1. (Look at the top card of your library. You may put that card on the bottom.)"

    spell {
        val t = target("target", TargetCreature(filter = TargetFilter.Creature.powerAtLeast(3)))
        effect = Effects.Composite(
            Effects.Destroy(t),
            Patterns.Library.scry(1)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "29"
        artist = "Chris Rallis"
        flavorText = "In the moment of truth, it is as though time stands still, and the whale waits, suspended in the sky."
        imageUri = "https://cards.scryfall.io/normal/front/5/4/54dd4948-dc79-4fe5-b4a0-fb257058f9dd.jpg?1783937228"
    }
}
