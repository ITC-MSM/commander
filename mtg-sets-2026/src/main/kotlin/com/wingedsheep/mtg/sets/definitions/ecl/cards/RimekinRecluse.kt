package com.wingedsheep.mtg.sets.definitions.ecl.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Rimekin Recluse
 * {2}{U}
 * Creature — Elemental Wizard
 * 3/2
 * When this creature enters, return up to one other target creature to its owner's hand.
 */
val RimekinRecluse = card("Rimekin Recluse") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Elemental Wizard"
    power = 3
    toughness = 2
    oracleText = "When this creature enters, return up to one other target creature to its owner's hand."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target(
            "other creature",
            TargetCreature(
                optional = true,
                filter = TargetFilter.OtherCreature
            )
        )
        effect = Effects.ReturnToHand(creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "66"
        artist = "Aurore Folny"
        flavorText = "Rimekin were once flamekin who turned their heat inward following the horrors of the Invasion. They burn cold, choosing isolation as the path to preserve their flames."
        imageUri = "https://cards.scryfall.io/normal/front/b/a/ba6b5368-3262-4002-bf1e-fce62f7f7901.jpg?1767957030"
    }
}
