package com.wingedsheep.mtg.sets.definitions.p02.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Screeching Drake
 * {3}{U}
 * Creature — Drake
 * 2 / 2
 *
 * Flying
 * When this creature enters, draw a card, then discard a card.
 *
 * The discard half is the [Patterns.Hand] recipe — gather hand, select one, move it to
 * the graveyard as a discard — so the prompt string stays the facade's own.
 */
val ScreechingDrake = card("Screeching Drake") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Drake"
    oracleText =
        "Flying\n" +
        "When this creature enters, draw a card, then discard a card."
    power = 2
    toughness = 2

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1) then Patterns.Hand.discardCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "44"
        artist = "Anson Maddocks"
        imageUri = "https://cards.scryfall.io/normal/front/6/c/6c16faac-b093-425b-b1e9-f71602d2f6dd.jpg"
    }
}
