package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Into the Roil
 * {1}{U}
 * Instant
 * Kicker {1}{U} (You may pay an additional {1}{U} as you cast this spell.)
 * Return target nonland permanent to its owner's hand. If this spell was kicked, draw a card.
 */
val IntoTheRoil = card("Into the Roil") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Kicker {1}{U} (You may pay an additional {1}{U} as you cast this spell.)\n" +
        "Return target nonland permanent to its owner's hand. If this spell was kicked, draw a card."

    keywordAbility(KeywordAbility.kicker("{1}{U}"))

    spell {
        target = Targets.NonlandPermanent
        effect = Effects.ReturnToHand(EffectTarget.ContextTarget(0)) then ConditionalEffect(
            condition = WasKicked,
            effect = Effects.DrawCards(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "48"
        artist = "Kieran Yanner"
        flavorText = "\"Roil tide! Roil tide! Tie yourselves down!\""
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5dba9972-dd8b-407b-9374-a8f0ed1a96db.jpg?1782715668"
    }
}
