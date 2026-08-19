package com.wingedsheep.mtg.sets.definitions.m12.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Buried Ruin
 * Land
 *
 * {T}: Add {C}.
 * {2}, {T}, Sacrifice this land: Return target artifact card from your graveyard to your hand.
 */
val BuriedRuin = card("Buried Ruin") {
    colorIdentity = ""
    typeLine = "Land"
    oracleText = "{T}: Add {C}.\n{2}, {T}, Sacrifice this land: Return target artifact card from your graveyard to your hand."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap, Costs.SacrificeSelf)
        val card = target(
            "target artifact card in your graveyard",
            TargetObject(filter = TargetFilter(GameObjectFilter.Artifact.ownedByYou(), zone = Zone.GRAVEYARD)),
        )
        effect = Effects.ReturnToHand(card)
        description = "{2}, {T}, Sacrifice this land: Return target artifact card from your graveyard " +
            "to your hand."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "224"
        artist = "Franz Vohwinkel"
        flavorText = "History has buried its treasures deep."
        imageUri = "https://cards.scryfall.io/normal/front/e/9/e910cf59-f7aa-44b1-bb8a-c2211179137c.jpg?1783941046"
    }
}
