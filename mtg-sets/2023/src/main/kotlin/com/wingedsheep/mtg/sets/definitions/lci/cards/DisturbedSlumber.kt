package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.MustBeBlockedEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Disturbed Slumber
 * {1}{G}
 * Instant — Common
 *
 * Until end of turn, target land you control becomes a 4/4 Dinosaur creature with reach and
 * haste. It's still a land. It must be blocked this turn if able.
 *
 * Two effects on resolution:
 *  1. [Effects.BecomeCreature] — Layer 4 adds CREATURE, Layer 4 sets subtypes to {Dinosaur},
 *     Layer 6 grants Reach + Haste, Layer 7b sets base P/T to 4/4. All revert at end of turn.
 *     LAND type is preserved (BecomeCreature does not strip it — "it's still a land").
 *  2. [MustBeBlockedEffect] — floating must-be-blocked requirement: the land-creature must be
 *     blocked this turn if able (one blocker is sufficient — `allCreatures = false`).
 */
val DisturbedSlumber = card("Disturbed Slumber") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Until end of turn, target land you control becomes a 4/4 Dinosaur creature with reach and haste. It's still a land. It must be blocked this turn if able."

    spell {
        val t = target(
            "target land you control",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.Land.youControl()))
        )
        effect = Effects.Composite(
            Effects.BecomeCreature(
                target = t,
                power = 4,
                toughness = 4,
                keywords = setOf(Keyword.REACH, Keyword.HASTE),
                creatureTypes = setOf("Dinosaur")
            ),
            MustBeBlockedEffect(t, allCreatures = false)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "182"
        artist = "David Palumbo"
        imageUri = "https://cards.scryfall.io/normal/front/4/4/4404b8d6-3673-4d82-b9ed-d28e9b54e1f6.jpg?1782694464"
    }
}
