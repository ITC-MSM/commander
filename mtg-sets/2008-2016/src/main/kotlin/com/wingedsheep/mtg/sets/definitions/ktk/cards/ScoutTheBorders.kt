package com.wingedsheep.mtg.sets.definitions.ktk.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Scout the Borders
 * {2}{G}
 * Sorcery
 * Reveal the top five cards of your library. You may put a creature or land card
 * from among them into your hand. Put the rest into your graveyard.
 *
 * `Patterns.Library.lookAtTopAndTakeMatching` is this sentence: the card used to restate the recipe
 * step by step, which only differed from it in the name of the pipeline collection.
 */
val ScoutTheBorders = card("Scout the Borders") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Reveal the top five cards of your library. You may put a creature or land card from among them into your hand. Put the rest into your graveyard."

    spell {
        effect = Patterns.Library.lookAtTopAndTakeMatching(
            count = DynamicAmount.Fixed(5),
            filter = GameObjectFilter.CreatureOrLand,
            prompt = "You may put a creature or land card from among them into your hand",
            revealed = true,
            restDestination = CardDestination.ToZone(Zone.GRAVEYARD),
            restOrder = CardOrder.Preserve
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "148"
        artist = "James Paick"
        flavorText = "\"I am in my element: the element of surprise.\" —Mogai, Sultai scout"
        imageUri = "https://cards.scryfall.io/normal/front/e/b/eb15c6c7-8fe6-496c-9977-3e7942b920c4.jpg?1562795470"
    }
}
