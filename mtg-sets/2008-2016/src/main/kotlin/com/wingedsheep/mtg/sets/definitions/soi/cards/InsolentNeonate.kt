package com.wingedsheep.mtg.sets.definitions.soi.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Insolent Neonate (Shadows over Innistrad #168)
 * {R}
 * Creature — Vampire
 * 1 / 1
 *
 * Menace (This creature can't be blocked except by two or more creatures.)
 * Discard a card, Sacrifice this creature: Draw a card.
 *
 * The activated ability has no mana cost at all — the whole cost is the discard plus the
 * self-sacrifice, which is why it doubles as a free madness/delirium outlet at instant speed.
 */
val InsolentNeonate = card("Insolent Neonate") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Vampire"
    power = 1
    toughness = 1
    oracleText = "Menace (This creature can't be blocked except by two or more creatures.)\n" +
        "Discard a card, Sacrifice this creature: Draw a card."

    keywords(Keyword.MENACE)

    activatedAbility {
        cost = Costs.Composite(Costs.DiscardCard, Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "168"
        artist = "Deruchenko Alexander"
        flavorText = "\"Manners are for mortals.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/1/813104f6-e6e4-4709-8626-12fe4262a11f.jpg?1783937748"
    }
}
