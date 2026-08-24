package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Refurbish
 * {3}{W}
 * Sorcery
 * Return target artifact card from your graveyard to the battlefield.
 *
 * [Effects.PutOntoBattlefieldFromGraveyard] rather than a plain put-onto-the-battlefield: it
 * carries the `fromZone = GRAVEYARD` guard, so the return is skipped if the card has left the
 * graveyard by the time the spell resolves.
 */
val Refurbish = card("Refurbish") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Return target artifact card from your graveyard to the battlefield."

    spell {
        val t = target(
            "target",
            TargetObject(filter = TargetFilter(GameObjectFilter.Artifact.ownedByYou(), zone = Zone.GRAVEYARD))
        )
        effect = Effects.PutOntoBattlefieldFromGraveyard(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "25"
        artist = "Johann Bodin"
        flavorText = "\"There's no reason to buy a new one when I've got the tools to fix the one you've got.\"\n—Ripu, repair specialist"
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f60e2ac4-f21f-4232-abc8-db078472408b.jpg?1783937229"
    }
}
