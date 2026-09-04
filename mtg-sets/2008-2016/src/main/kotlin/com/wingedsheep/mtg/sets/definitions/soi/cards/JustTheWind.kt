package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.madness
import com.wingedsheep.sdk.model.Rarity

/**
 * Just the Wind (Shadows over Innistrad #71)
 * {1}{U}
 * Instant
 *
 * Return target creature to its owner's hand.
 * Madness {U} (If you discard this card, discard it into exile. When you do, cast it for its madness cost or put it into your graveyard.)
 *
 * A plain bounce: [Effects.ReturnToHand] already sends the permanent to its *owner's* hand, so
 * stealing a creature and bouncing it doesn't keep the card. Madness (CR 702.35) is the reason
 * the spell reads as a one-mana trick — the cast happens while the madness trigger resolves.
 */
val JustTheWind = card("Just the Wind") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Return target creature to its owner's hand.\n" +
        "Madness {U} (If you discard this card, discard it into exile. When you do, cast it for its madness cost or put it into your graveyard.)"

    spell {
        val creature = target("target", Targets.Creature)
        effect = Effects.ReturnToHand(creature)
    }

    madness("{U}")

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "71"
        artist = "Christopher Moeller"
        flavorText = "\"There's nothing to worry about.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/8/98b3d18c-7029-4eff-a918-f75e7d9d79d7.jpg?1783937795"
    }
}
