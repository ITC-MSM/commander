package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Spirit Water Revival
 * {1}{U}{U}
 * Sorcery
 * As an additional cost to cast this spell, you may waterbend {6}. (While paying a waterbend cost,
 * you can tap your artifacts and creatures to help. Each one pays for {1}.)
 * Draw two cards. If this spell's additional cost was paid, instead shuffle your graveyard into
 * your library, draw seven cards, and you have no maximum hand size for the rest of the game.
 * Exile Spirit Water Revival.
 */
val SpiritWaterRevival = card("Spirit Water Revival") {
    manaCost = "{1}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "As an additional cost to cast this spell, you may waterbend {6}. " +
        "(While paying a waterbend cost, you can tap your artifacts and creatures to help. " +
        "Each one pays for {1}.)\n" +
        "Draw two cards. If this spell's additional cost was paid, instead shuffle your graveyard " +
        "into your library, draw seven cards, and you have no maximum hand size for the rest of " +
        "the game.\n" +
        "Exile Spirit Water Revival."

    waterbendCost(amount = 6, optional = true)

    spell {
        selfExile()
        effect = ConditionalEffect(
            condition = Conditions.WaterbendWasPaid,
            effect = Effects.Composite(
                Patterns.Library.shuffleGraveyardIntoLibrary(EffectTarget.Controller),
                Effects.DrawCards(7),
                Effects.RemoveMaximumHandSize()
            ),
            elseEffect = Effects.DrawCards(2)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "73"
        artist = "Enishi"
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0c019e76-c88e-4d1b-a546-0f4e462ef44a.jpg?1764120466"
    }
}
