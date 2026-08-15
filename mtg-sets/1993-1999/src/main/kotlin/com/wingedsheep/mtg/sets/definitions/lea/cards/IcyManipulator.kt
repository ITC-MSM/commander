package com.wingedsheep.mtg.sets.definitions.lea.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Icy Manipulator
 * {4}
 * Artifact
 * {1}, {T}: Tap target artifact, creature, or land.
 */
val IcyManipulator = card("Icy Manipulator") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{1}, {T}: Tap target artifact, creature, or land."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap)
        val permanent = target(
            "artifact, creature, or land",
            TargetObject(
                filter = TargetFilter(
                    GameObjectFilter.Artifact or GameObjectFilter.Creature or GameObjectFilter.Land
                )
            )
        )
        effect = Effects.Tap(permanent)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "248"
        artist = "Douglas Shuler"
        imageUri = "https://cards.scryfall.io/normal/front/2/9/29dc1596-a2e7-4d60-9f99-89babaef8a06.jpg?1559591386"
    }
}
