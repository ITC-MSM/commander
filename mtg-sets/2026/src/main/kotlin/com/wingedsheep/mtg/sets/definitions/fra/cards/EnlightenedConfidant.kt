package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect

/**
 * Enlightened Confidant — "if you gained life this turn" is an intervening-if
 * ([interveningIf]), checked when the end step begins and again on resolution.
 *
 * The surveil remembers the card it put into the graveyard (`surveiledIntoGraveyard`); the
 * payoff moves that card to hand only when its mana value is at most the life gained this turn,
 * read on resolution.
 */
val EnlightenedConfidant = card("Enlightened Confidant") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kor Cleric"
    power = 2
    toughness = 1
    oracleText = "Lifelink\n" +
        "At the beginning of your end step, if you gained life this turn, surveil 1. If you put a card " +
        "with mana value less than or equal to the amount of life you gained this turn into your " +
        "graveyard this way, put that card into your hand."

    keywords(Keyword.LIFELINK)

    triggeredAbility {
        trigger = Triggers.YourEndStep
        interveningIf = Conditions.YouGainedLifeThisTurn
        effect = Effects.Composite(
            Patterns.Library.surveil(1, storeGraveyardAs = "surveiledIntoGraveyard"),
            MoveCollectionEffect(
                from = "surveiledIntoGraveyard",
                destination = CardDestination.ToZone(Zone.HAND),
                filter = GameObjectFilter.Any.manaValueAtMostDynamic(DynamicAmounts.lifeGainedThisTurn())
            )
        )
        description = "At the beginning of your end step, if you gained life this turn, surveil 1. If you " +
            "put a card with mana value less than or equal to the amount of life you gained this turn " +
            "into your graveyard this way, put that card into your hand."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "5"
        artist = "Denman Rooke"
        flavorText = "Greatness is earned, not bought."
        imageUri = "https://cards.scryfall.io/normal/front/4/8/483fcc58-cc6e-4452-a696-7b38e117c837.jpg?1788329184"
        inBooster = false
    }
}
