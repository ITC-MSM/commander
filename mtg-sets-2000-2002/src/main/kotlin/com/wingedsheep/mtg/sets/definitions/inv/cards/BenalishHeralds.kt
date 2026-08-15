package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Benalish Heralds
 * {3}{W}
 * Creature — Human Soldier
 * 2/4
 *
 * {3}{U}, {T}: Draw a card.
 */
val BenalishHeralds = card("Benalish Heralds") {
    manaCost = "{3}{W}"
    colorIdentity = "WU"
    typeLine = "Creature — Human Soldier"
    power = 2
    toughness = 4
    oracleText = "{3}{U}, {T}: Draw a card."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{U}"), Costs.Tap)
        effect = Effects.DrawCards(1)
        description = "{3}{U}, {T}: Draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "6"
        artist = "Don Hazeltine"
        imageUri = "https://cards.scryfall.io/normal/front/1/3/13c6e51d-54eb-4e5b-9ec9-54521b16b8d1.jpg?1562898954"
    }
}
