package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Wall of Omens
 * {1}{W}
 * Creature — Wall
 * 0 / 4
 *
 * Defender
 * When this creature enters, draw a card.
 *
 * Modeling notes:
 *  - Defender is the bare keyword — the engine's combat code reads `Keyword.DEFENDER` off the card,
 *    so `keywords(...)` alone is the whole ability (the corpus convention; a separate
 *    `keywordAbility` row would only duplicate it).
 *  - "**When** this creature enters" is the one-shot [Triggers.EntersBattlefield].
 *  - "Draw a card" is untargeted and drawn by the controller, which is [Effects.DrawCards]'s
 *    default recipient, so no target argument is written.
 */
val WallOfOmens = card("Wall of Omens") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Wall"
    power = 0
    toughness = 4
    oracleText = "Defender\n" +
            "When this creature enters, draw a card."

    keywords(Keyword.DEFENDER)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
        description = "When this creature enters, draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "53"
        artist = "James Paick"
        flavorText = "\"I search for a vision of Zendikar that does not include the Eldrazi.\"\n—Expedition journal entry"
        imageUri = "https://cards.scryfall.io/normal/front/6/e/6e3510f5-e450-400b-98ea-341dbf212054.jpg?1783942000"
    }
}
