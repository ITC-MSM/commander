package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantCantBeCountered

/**
 * Chimil, the Inner Sun
 * {6}
 * Legendary Artifact
 * Spells you control can't be countered.
 * At the beginning of your end step, discover 5.
 */
val ChimilTheInnerSun = card("Chimil, the Inner Sun") {
    manaCost = "{6}"
    typeLine = "Legendary Artifact"
    oracleText = "Spells you control can't be countered.\nAt the beginning of your end step, discover 5."

    staticAbility {
        ability = GrantCantBeCountered(filter = GameObjectFilter.Any.youControl())
    }

    triggeredAbility {
        trigger = Triggers.YourEndStep
        effect = Effects.Discover(5)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "249"
        artist = "Adam Paquette"
        imageUri = "https://cards.scryfall.io/normal/front/2/7/27a1bfb5-ddfc-49cf-baa3-5d1958d2067a.jpg?1782694413"
    }
}
