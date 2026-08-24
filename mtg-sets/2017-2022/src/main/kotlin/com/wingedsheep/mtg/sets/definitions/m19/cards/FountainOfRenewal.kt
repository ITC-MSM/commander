package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Fountain of Renewal
 * {1}
 * Artifact
 * At the beginning of your upkeep, you gain 1 life.
 * {3}, Sacrifice this artifact: Draw a card.
 *
 * [Triggers.YourUpkeep] is the printed "at the beginning of your upkeep" — a `StepEvent` on
 * `Step.UPKEEP` for `Player.You`. The second line is a plain activated ability whose cost is
 * {3} plus [Costs.SacrificeSelf].
 */
val FountainOfRenewal = card("Fountain of Renewal") {
    manaCost = "{1}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "At the beginning of your upkeep, you gain 1 life.\n" +
        "{3}, Sacrifice this artifact: Draw a card."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.GainLife(1)
        description = "At the beginning of your upkeep, you gain 1 life."
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.SacrificeSelf)
        effect = Effects.DrawCards(1)
        description = "{3}, Sacrifice this artifact: Draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "235"
        artist = "Adam Paquette"
        flavorText = "Entrepreneurs have attempted to sell the water, but to no avail. Whatever magic it contains disappears upon bottling."
        imageUri = "https://cards.scryfall.io/normal/front/2/6/26894980-8961-4479-85dd-5f01c899718b.jpg"
    }
}
