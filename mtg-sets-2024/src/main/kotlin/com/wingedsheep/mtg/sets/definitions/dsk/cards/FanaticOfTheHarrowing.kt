package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fanatic of the Harrowing
 * {3}{B}
 * Creature — Human Cleric
 * 2/2
 *
 * When this creature enters, each player discards a card. If you discarded a card this way, draw a card.
 *
 * Modeled as an enters trigger. Each player discards one card: opponents via
 * [Patterns.Hand.eachOpponentDiscards], and the controller's discard is wrapped in an
 * [Effects.IfYouDo] so the follow-up draw only happens when the controller actually had a card to
 * discard (CR — "If you discarded a card this way"). [SuccessCriterion.Auto] infers the gate from
 * the discard pipeline's terminal move to the graveyard, so an empty-handed controller draws
 * nothing. A player with an empty hand simply doesn't discard.
 */
val FanaticOfTheHarrowing = card("Fanatic of the Harrowing") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Cleric"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, each player discards a card. If you discarded a card " +
        "this way, draw a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            Patterns.Hand.eachOpponentDiscards(1),
            Effects.IfYouDo(
                action = Patterns.Hand.discardCards(1),
                ifYouDo = Effects.DrawCards(1),
            ),
        )
        description = "When this creature enters, each player discards a card. If you discarded a " +
            "card this way, draw a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "96"
        artist = "Fajareka Setiawan"
        flavorText = "\"Valgavoth wants our pain, our terror! We cannot join him unless we suffer!\""
        imageUri = "https://cards.scryfall.io/normal/front/2/a/2a0acb05-91e0-4f7c-b48b-99e1068fad16.jpg?1726286207"
    }
}
