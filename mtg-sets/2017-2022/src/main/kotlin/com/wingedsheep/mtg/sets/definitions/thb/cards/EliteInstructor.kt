package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Elite Instructor
 * {2}{U}
 * Creature — Human Wizard
 * 2/2
 * When this creature enters, draw a card, then discard a card.
 */
val EliteInstructor = card("Elite Instructor") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, draw a card, then discard a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Hand.loot()
        description = "When this creature enters, draw a card, then discard a card."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "49"
        artist = "Mike Sass"
        flavorText = "The greatest minds in Meletis study under the masters at the Dekatia, a renowned school of magic and philosophy."
        imageUri = "https://cards.scryfall.io/normal/front/8/2/821cd2dd-aa03-4c55-b9e4-98e0284889d3.jpg?1783931585"
    }
}
