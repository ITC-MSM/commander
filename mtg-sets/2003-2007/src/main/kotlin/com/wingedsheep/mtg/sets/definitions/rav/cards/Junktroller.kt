package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Junktroller
 * {4}
 * Artifact Creature — Golem
 * 0/6
 * Defender
 * {T}: Put target card from a graveyard on the bottom of its owner's library.
 *
 * "A graveyard" is any graveyard and "card" is any card type, so the target is the unrestricted
 * [TargetFilter.CardInGraveyard]. The move is [Effects.Move] to [Zone.LIBRARY] at
 * [ZonePlacement.Bottom] — cards moved to a library land in their *owner's* one, which is what the
 * printed "its owner's library" asks for.
 */
val Junktroller = card("Junktroller") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Golem"
    oracleText = "Defender\n{T}: Put target card from a graveyard on the bottom of its owner's library."
    power = 0
    toughness = 6
    keywords(Keyword.DEFENDER)
    activatedAbility {
        cost = Costs.Tap
        val t = target("target", TargetObject(filter = TargetFilter.CardInGraveyard))
        effect = Effects.Move(t, Zone.LIBRARY, ZonePlacement.Bottom)
        description = "Put target card from a graveyard on the bottom of its owner's library."
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "264"
        artist = "Chippy"
        flavorText = "One man's trash is another man's troller."
        imageUri = "https://cards.scryfall.io/normal/front/5/e/5e2c5f0a-755f-4d4f-b5f8-a938562797f9.jpg"
    }
}
