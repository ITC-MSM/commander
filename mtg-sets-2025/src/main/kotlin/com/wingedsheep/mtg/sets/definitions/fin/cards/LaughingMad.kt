package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Laughing Mad
 * {2}{R}
 * Instant
 * As an additional cost to cast this spell, discard a card.
 * Draw two cards.
 * Flashback {3}{R} (You may cast this card from your graveyard for its flashback cost and
 * any additional costs. Then exile it.)
 *
 * The discard additional cost applies on every cast, including the flashback cast — the
 * engine evaluates `additionalCost` as part of the spell's casting requirements regardless
 * of which zone it is cast from.
 */
val LaughingMad = card("Laughing Mad") {
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
        collectorNumber = "143"
        artist = "RARE ENGINE"
        imageUri = "https://cards.scryfall.io/normal/front/c/d/cd5b9daf-6325-4eb4-a069-4a8cc7807884.jpg?1748706298"
    }
}
