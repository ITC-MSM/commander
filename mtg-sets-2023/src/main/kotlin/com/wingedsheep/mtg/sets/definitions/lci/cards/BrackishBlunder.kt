package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Brackish Blunder — LCI #46
 * {1}{U} Instant — Common
 *
 * "Return target creature to its owner's hand. If it was tapped, create a Map token.
 *  (It's an artifact with "{1}, {T}, Sacrifice this token: Target creature you control explores.
 *  Activate only as a sorcery.")"
 *
 * Timing note: [Conditions.TargetIsTapped] reads live battlefield state and returns false once
 * the permanent is no longer in play. The condition must therefore be evaluated while the creature
 * is still on the battlefield — before the bounce. [ConditionalEffect] handles this correctly:
 * it checks "tapped?" against the pre-resolution state, then both branches bounce the creature
 * (the tapped branch also creates a Map token).
 */
val BrackishBlunder = card("Brackish Blunder") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Return target creature to its owner's hand. If it was tapped, create a Map token. " +
        "(It's an artifact with \"{1}, {T}, Sacrifice this token: Target creature you control explores. " +
        "Activate only as a sorcery.\")"

    spell {
        val t = target("target creature", Targets.Creature)
        effect = ConditionalEffect(
            condition = Conditions.TargetIsTapped(0),
            effect = Effects.ReturnToHand(t) then Effects.CreateMapToken(),
            elseEffect = Effects.ReturnToHand(t),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "46"
        artist = "Daarken"
        imageUri = "https://cards.scryfall.io/normal/front/d/e/dea3d0f4-76f5-416f-93ba-8b003b967816.jpg?1782694573"
    }
}
