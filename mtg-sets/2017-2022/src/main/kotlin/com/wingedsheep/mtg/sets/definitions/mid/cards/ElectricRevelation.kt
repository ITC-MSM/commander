package com.wingedsheep.mtg.sets.definitions.mid.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Electric Revelation
 * {2}{R}
 * Instant
 * As an additional cost to cast this spell, discard a card.
 * Draw two cards.
 * Flashback {3}{R} (You may cast this card from your graveyard for its flashback cost and any additional costs. Then exile it.)
 *
 * Thrill of Possibility plus flashback: the additional discard is declared with
 * [Costs.additional] so it is paid on both the normal cast and the flashback cast.
 */
val ElectricRevelation = card("Electric Revelation") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Instant"
    oracleText = "As an additional cost to cast this spell, discard a card.\nDraw two cards.\nFlashback {3}{R} (You may cast this card from your graveyard for its flashback cost and any additional costs. Then exile it.)"

    additionalCost(Costs.additional.DiscardCards())

    spell {
        effect = Effects.DrawCards(2)
    }

    keywordAbility(KeywordAbility.flashback("{3}{R}"))

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "135"
        artist = "Liiga Smilshkalne"
        imageUri = "https://cards.scryfall.io/normal/front/2/0/20ff3289-00fb-4e91-b34f-6255e9de8e9e.jpg?1783925602"
        ruling(
            "2021-09-24",
            "If you cast Electric Revelation using flashback, you must still pay its additional cost of discarding a card."
        )
    }
}
