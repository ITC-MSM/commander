package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.DrawCardsEffect
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Splitskin Doll
 * {1}{W}
 * Artifact Creature — Toy
 * 2/1
 * When this creature enters, draw a card. Then discard a card unless you control
 * another creature with power 2 or less.
 */
val SplitskinDoll = card("Splitskin Doll") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Artifact Creature — Toy"
    oracleText = "When this creature enters, draw a card. Then discard a card unless you control another creature with power 2 or less."
    power = 2
    toughness = 1

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        // "draw a card. Then discard a card unless you control another creature with power 2 or
        // less." Modeled as draw, then a conditional discard gated on NOT controlling such a
        // creature — the discard only happens when the "unless" clause is unmet.
        effect = Effects.Composite(
            DrawCardsEffect(1),
            ConditionalEffect(
                condition = Conditions.Not(
                    Conditions.YouControl(
                        GameObjectFilter.Creature.powerAtMost(2),
                        excludeSelf = true
                    )
                ),
                effect = Patterns.Hand.discardCards(1)
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "33"
        artist = "Diana Franco"
        flavorText = "Those who mock its ragged surface and seeping eyes have a curious tendency to go missing."
        imageUri = "https://cards.scryfall.io/normal/front/0/4/0453be94-e59b-48f1-a488-7a9fd96a627e.jpg?1726285979"
    }
}
