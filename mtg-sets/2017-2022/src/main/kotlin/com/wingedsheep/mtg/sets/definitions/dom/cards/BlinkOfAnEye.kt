package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect

/**
 * Blink of an Eye
 * {1}{U}
 * Instant
 * Kicker {1}{U}
 * Return target nonland permanent to its owner's hand.
 * If this spell was kicked, draw a card.
 */
val BlinkOfAnEye = card("Blink of an Eye") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Kicker {1}{U}\nReturn target nonland permanent to its owner's hand. If this spell was kicked, draw a card."

    keywordAbility(KeywordAbility.kicker("{1}{U}"))

    spell {
        val t = target("target", Targets.NonlandPermanent)
        effect = Effects.ReturnToHand(t)
            .then(
                ConditionalEffect(
                    condition = WasKicked,
                    effect = Effects.DrawCards(1)
                )
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "46"
        artist = "Igor Kieryluk"
        imageUri = "https://cards.scryfall.io/normal/front/a/b/ab830392-4d7e-4b45-93cf-35ed1e935228.jpg?1562741100"
    }
}
