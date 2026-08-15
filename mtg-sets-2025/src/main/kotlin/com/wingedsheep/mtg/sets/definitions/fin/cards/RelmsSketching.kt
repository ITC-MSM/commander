package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Relm's Sketching
 * {2}{U}{U}
 * Sorcery
 * Create a token that's a copy of target artifact, creature, or land.
 *
 * Targets any one artifact, creature, or land permanent (the union filter), then makes a token
 * copy of it via [Effects.CreateTokenCopyOfTarget], which copies that permanent's copiable
 * characteristics (Rule 707.2) for whichever permanent type was chosen.
 */
val RelmsSketching = card("Relm's Sketching") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Create a token that's a copy of target artifact, creature, or land."

    spell {
        val permanent = target(
            "target artifact, creature, or land",
            TargetPermanent(
                filter = TargetFilter(
                    GameObjectFilter.Artifact or GameObjectFilter.Creature or GameObjectFilter.Land
                )
            )
        )
        effect = Effects.CreateTokenCopyOfTarget(target = permanent)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "67"
        artist = "Smirtouille"
        flavorText = "In her pictures she captures everything: forests, water, light . . . the very essence of the things she paints."
        imageUri = "https://cards.scryfall.io/normal/front/6/a/6aedac12-3714-4a81-bd4d-1d2555c66f78.jpg?1748706008"
    }
}
