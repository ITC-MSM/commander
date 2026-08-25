package com.wingedsheep.mtg.sets.definitions.mh1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Weather the Storm
 * {1}{G}
 * Instant
 * You gain 3 life.
 * Storm (When you cast this spell, copy it for each spell cast before it this turn.)
 *
 * Sprouting Vines' shape: [Keyword.STORM] is read by the cast pipeline, which puts the copies on
 * the stack, so the bare keyword is the whole of the second line and the body is one
 * [Effects.GainLife] whose default target is the controller.
 */
val WeatherTheStorm = card("Weather the Storm") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "You gain 3 life.\n" +
        "Storm (When you cast this spell, copy it for each spell cast before it this turn.)"

    keywords(Keyword.STORM)

    spell {
        effect = Effects.GainLife(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "191"
        artist = "Magali Villeneuve"
        flavorText = "\"Quell your ego and anywhere can be as calm as a hurricane's eye.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/6/f6a9fa51-78c3-42e6-8c2e-39658f59ed87.jpg?1783933087"
    }
}
