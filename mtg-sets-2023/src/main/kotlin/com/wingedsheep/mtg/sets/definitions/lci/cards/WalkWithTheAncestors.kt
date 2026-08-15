package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Walk with the Ancestors
 * {4}{G}
 * Sorcery
 * Return up to one target permanent card from your graveyard to your hand.
 * Discover 4.
 */
val WalkWithTheAncestors = card("Walk with the Ancestors") {
    manaCost = "{4}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Return up to one target permanent card from your graveyard to your hand.\nDiscover 4."
    spell {
        target(
            "up to one target permanent card from your graveyard",
            TargetObject(
                count = 1,
                optional = true,
                filter = TargetFilter(GameObjectFilter.Permanent.ownedByYou(), zone = Zone.GRAVEYARD)
            )
        )
        effect = Effects.Composite(
            Effects.ReturnToHand(EffectTarget.ContextTarget(0)),
            Effects.Discover(4)
        )
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "218"
        artist = "Loïc Canavaggia"
        imageUri = "https://cards.scryfall.io/normal/front/7/1/71a6f85b-5ef8-4526-9c86-7cb71508b4c0.jpg?1782694435"
    }
}
