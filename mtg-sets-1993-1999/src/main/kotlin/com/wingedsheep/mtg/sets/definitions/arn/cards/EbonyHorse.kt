package com.wingedsheep.mtg.sets.definitions.arn.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Ebony Horse
 * {3}
 * Artifact
 * {2}, {T}: Untap target attacking creature you control. Prevent all combat damage that
 * would be dealt to and by that creature this turn.
 */
val EbonyHorse = card("Ebony Horse") {
    manaCost = "{3}"
    typeLine = "Artifact"
    oracleText = "{2}, {T}: Untap target attacking creature you control. Prevent all combat damage that would be dealt to and by that creature this turn."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        val creature = target(
            "target attacking creature you control",
            TargetObject(filter = TargetFilter(GameObjectFilter.Creature.attacking().youControl()))
        )
        effect = Effects.Untap(creature).then(Effects.PreventCombatDamageToAndBy(creature))
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "62"
        artist = "Dameon Willich"
        imageUri = "https://cards.scryfall.io/normal/front/9/a/9ae81ec7-2b7d-4301-8114-032be5e6b663.jpg?1562923807"
    }
}
