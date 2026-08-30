package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Archwing Dragon
 * {2}{R}{R}
 * Creature — Dragon
 * 4 / 4
 *
 * Flying, haste
 * At the beginning of the end step, return this creature to its owner's hand.
 *
 * "The end step" carries no possessive, so the trigger is [Triggers.EachEndStep]
 * (`StepEvent(Step.END, Player.Each)`) — the Dragon bounces at the end of *every* turn, not only
 * its controller's.
 */
val ArchwingDragon = card("Archwing Dragon") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Dragon"
    power = 4
    toughness = 4
    oracleText = "Flying, haste\n" +
        "At the beginning of the end step, return this creature to its owner's hand."

    keywords(Keyword.FLYING, Keyword.HASTE)

    triggeredAbility {
        trigger = Triggers.EachEndStep
        effect = Effects.Move(EffectTarget.Self, Zone.HAND)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "126"
        artist = "Daarken"
        flavorText = "Swifter than an angel, crueler than a demon, and relentless as a ghoul."
        imageUri = "https://cards.scryfall.io/normal/front/6/c/6c6f1a8b-329e-4094-8141-6bc88311a08c.jpg?1783940691"
    }
}
