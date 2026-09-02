package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Aether Helix — Strixhaven: School of Mages #162 (canonical printing)
 * {3}{G}{U} · Sorcery
 *
 * Return target permanent to its owner's hand. Return target permanent card from your graveyard to your hand.
 *
 * Two independent targets, each bounced by [Effects.ReturnToHand] in printed order: the first is
 * any permanent on the battlefield ([Targets.Permanent]), the second is the Nature's Spiral shape —
 * a permanent card you own, zoned to your graveyard. Both are required targets, so the spell needs
 * a legal choice for each to be cast (CR 601.2c).
 */
val AetherHelix = card("Aether Helix") {
    manaCost = "{3}{G}{U}"
    colorIdentity = "GU"
    typeLine = "Sorcery"
    oracleText =
        "Return target permanent to its owner's hand. Return target permanent card from your graveyard to your hand."

    spell {
        val permanent = target("target", Targets.Permanent)
        val graveyardCard = target(
            "target 1",
            TargetObject(
                filter = TargetFilter(GameObjectFilter.Permanent.ownedByYou(), zone = Zone.GRAVEYARD)
            )
        )
        effect = Effects.ReturnToHand(permanent) then Effects.ReturnToHand(graveyardCard)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "162"
        artist = "Torstein Nordstrand"
        flavorText = "Some Quandrix mages prefer to study at twilight, watching their experimental equations trace glowing trails against the encroaching darkness."
        imageUri = "https://cards.scryfall.io/normal/front/7/d/7d1c653e-f629-4105-a52c-379c5cd78208.jpg?1783927325"
    }
}
