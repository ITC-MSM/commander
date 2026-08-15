package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Nanoform Sentinel
 * {2}{U}
 * Artifact Creature — Robot
 * 3/2
 *
 * Whenever this creature becomes tapped, untap another target permanent. This ability triggers only once each turn.
 */
val NanoformSentinel = card("Nanoform Sentinel") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Artifact Creature — Robot"
    oracleText = "Whenever this creature becomes tapped, untap another target permanent. This ability triggers only once each turn."
    power = 3
    toughness = 2

    // Trigger: whenever this creature becomes tapped, untap another target permanent (once per turn)
    triggeredAbility {
        trigger = Triggers.BecomesTapped
        oncePerTurn = true
        val targetPermanent = target("another target permanent", TargetPermanent(
            filter = TargetFilter(GameObjectFilter.Permanent, excludeSelf = true)
        ))
        effect = Effects.Untap(targetPermanent)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "71"
        artist = "Tianxing Xu"
        flavorText = "Cloudsculpting naturally led the Illvoi to other methods of reshaping matter."
        imageUri = "https://cards.scryfall.io/normal/front/3/e/3eeae8c3-7939-4c79-92f0-fbdb9c1b71d3.jpg?1752946838"
    }
}
