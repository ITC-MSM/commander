package com.wingedsheep.mtg.sets.definitions.neo.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Circuit Mender
 * {3}
 * Artifact Creature — Insect
 * 2/3
 * When this creature enters, you gain 2 life.
 * When this creature leaves the battlefield, draw a card.
 *
 * Two independent SELF zone-change triggers. The second is [Triggers.LeavesBattlefield], not
 * [Triggers.Dies] — bouncing, exiling, or milling it off the battlefield all draw the card.
 */
val CircuitMender = card("Circuit Mender") {
    manaCost = "{3}"
    colorIdentity = ""
    typeLine = "Artifact Creature — Insect"
    power = 2
    toughness = 3
    oracleText = "When this creature enters, you gain 2 life.\nWhen this creature leaves the battlefield, draw a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(2)
        description = "When this creature enters, you gain 2 life."
    }

    triggeredAbility {
        trigger = Triggers.LeavesBattlefield
        effect = Effects.DrawCards(1)
        description = "When this creature leaves the battlefield, draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "242"
        artist = "Hector Ortiz"
        flavorText = "Inspired by industrious silkworms, its maker crafted it to restore the broken pieces of the world."
        imageUri = "https://cards.scryfall.io/normal/front/d/e/defaeb68-3f8a-4740-b13f-8c71c7e9c8b4.jpg?1783923827"
    }
}
