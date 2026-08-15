package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent


/**
 * Lunatic Pandora
 * {1}
 * Legendary Artifact
 * {2}, {T}: Surveil 1. (Look at the top card of your library. You may put it into your graveyard.)
 * {6}, {T}, Sacrifice Lunatic Pandora: Destroy target nonland permanent.
 */
val LunaticPandora = card("Lunatic Pandora") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Legendary Artifact"
    oracleText = "{2}, {T}: Surveil 1. (Look at the top card of your library. You may put it into your graveyard.)\n{6}, {T}, Sacrifice Lunatic Pandora: Destroy target nonland permanent."
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{2}"), Costs.Tap)
        effect = Patterns.Library.surveil(1)
    }
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{6}"), Costs.Tap, Costs.SacrificeSelf)
        val t = target("target", TargetPermanent(filter = TargetFilter.NonlandPermanent))
        effect = Effects.Move(t, Zone.GRAVEYARD, byDestruction = true)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "262"
        artist = "Enora Mercier"
        flavorText = "\"Inside the Lunatic Pandora is this thing called a Crystal Pillar. It calls monsters from the Moon.\"\n—Zell Dincht"
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d6e1e3e7-20d4-42cb-ad22-60356b9e8fdc.jpg"
    }
}
