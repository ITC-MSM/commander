package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Hana Kami
 * {G}
 * Creature — Spirit
 * 1/1
 *
 * {1}{G}, Sacrifice this creature: Return target Arcane card from your graveyard to your hand.
 *
 * Argentum Assay declines this one — its grammar has no rule for the noun phrase "an Arcane card" —
 * so it is authored straight from the printed text. The graveyard target is the bare-noun-subtype
 * spelling [TargetFilter.CardInGraveyard]`.withSubtype(Subtype.ARCANE).ownedByYou()`, the same shape
 * Lord of the Undead and Misery Charm use for their tribal graveyard retrieval; the cost is the
 * ordinary mana + sacrifice-self composite.
 */
val HanaKami = card("Hana Kami") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spirit"
    power = 1
    toughness = 1
    oracleText = "{1}{G}, Sacrifice this creature: Return target Arcane card from your graveyard to your hand."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{G}"), Costs.SacrificeSelf)
        val t = target(
            "target",
            TargetObject(filter = TargetFilter.CardInGraveyard.withSubtype(Subtype.ARCANE).ownedByYou())
        )
        effect = Effects.Move(t, Zone.HAND)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "211"
        artist = "Rebecca Guay"
        flavorText = "It grew in lands lit by pride and watered by tears."
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a8304b24-4db8-4082-bdb7-7010bf35e416.jpg?1783944290"
    }
}
