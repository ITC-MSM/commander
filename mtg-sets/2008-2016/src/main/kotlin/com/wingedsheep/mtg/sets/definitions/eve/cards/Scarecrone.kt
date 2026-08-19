package com.wingedsheep.mtg.sets.definitions.eve.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Scarecrone
 * {3}
 * Artifact Creature — Scarecrow
 * 1/2
 *
 * {1}, Sacrifice a Scarecrow: Draw a card.
 * {4}, {T}: Return target artifact creature card from your graveyard to the battlefield.
 */
val Scarecrone = card("Scarecrone") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Scarecrow"
    oracleText = "{1}, Sacrifice a Scarecrow: Draw a card.\n{4}, {T}: Return target artifact creature card from your graveyard to the battlefield."
    power = 1
    toughness = 2

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.Sacrifice(GameObjectFilter.Permanent.withSubtype(Subtype.SCARECROW)),
        )
        effect = Effects.DrawCards(1)
        description = "{1}, Sacrifice a Scarecrow: Draw a card."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{4}"), Costs.Tap)
        val card = target(
            "target artifact creature card in your graveyard",
            TargetObject(
                filter = TargetFilter(GameObjectFilter.ArtifactCreature.ownedByYou(), zone = Zone.GRAVEYARD)
            ),
        )
        effect = Effects.PutOntoBattlefieldFromGraveyard(card)
        description = "{4}, {T}: Return target artifact creature card from your graveyard to the battlefield."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "172"
        artist = "Jesper Ejsing"
        flavorText = "Her poppets bring joy to the truly depraved."
        imageUri = "https://cards.scryfall.io/normal/front/1/9/1978400d-a8f1-4df7-8caf-b9ca81334bce.jpg?1783942656"
    }
}
