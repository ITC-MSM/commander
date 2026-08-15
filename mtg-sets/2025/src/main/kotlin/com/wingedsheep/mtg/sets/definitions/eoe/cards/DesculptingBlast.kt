package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Desculpting Blast
 * {1}{U}
 * Instant
 * Return target nonland permanent to its owner's hand. If it was attacking,
 * create a 1/1 colorless Drone artifact creature token with flying and
 * "This token can block only creatures with flying."
 */
val DesculptingBlast = card("Desculpting Blast") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Return target nonland permanent to its owner's hand. If it was attacking, create a 1/1 colorless Drone artifact creature token with flying and \"This token can block only creatures with flying.\""

    spell {
        val permanent = target("target nonland permanent", Targets.NonlandPermanent)
        // Check attacking status before the return so the permanent is still on the battlefield
        effect = Effects.Composite(
            ConditionalEffect(
                condition = Conditions.TargetMatchesFilter(GameObjectFilter.Permanent.attacking()),
                effect = Effects.CreateDroneToken()
            ),
            Effects.ReturnToHand(permanent)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "54"
        artist = "Jeremy Wilson"
        flavorText = "\"Your atoms will serve my ambitions in a much more useful form.\""
        imageUri = "https://cards.scryfall.io/normal/front/7/7/77fdbf32-b5f4-4346-846d-d8e0e53e6e53.jpg?1752946764"
    }
}
