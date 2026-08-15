package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Fire of Orthanc
 * {3}{R}
 * Sorcery
 *
 * Destroy target artifact or land. Creatures without flying can't block this turn.
 */
val FireOfOrthanc = card("Fire of Orthanc") {
    manaCost = "{3}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Destroy target artifact or land. Creatures without flying can't block this turn."

    spell {
        val t = target(
            "artifact or land",
            TargetPermanent(filter = TargetFilter(GameObjectFilter.Artifact.or(GameObjectFilter.Land)))
        )
        effect = Effects.Destroy(t)
            .then(
                Effects.CantBlockGroup(
                    GroupFilter(GameObjectFilter.Creature.withoutKeyword(Keyword.FLYING))
                )
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "127"
        artist = "Jeremy Paillotin"
        flavorText = "The barricade was scattered as if by a thunderbolt."
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9b5871c5-bb94-4803-93ba-cd1a630b00d6.jpg?1686968936"
    }
}
