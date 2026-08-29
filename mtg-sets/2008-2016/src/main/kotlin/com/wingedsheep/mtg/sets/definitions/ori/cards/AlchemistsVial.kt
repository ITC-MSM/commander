package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Alchemist's Vial
 * {2}
 * Artifact
 *
 * When this artifact enters, draw a card.
 * {1}, {T}, Sacrifice this artifact: Target creature can't attack or block this turn.
 */
val AlchemistsVial = card("Alchemist's Vial") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "When this artifact enters, draw a card.\n" +
        "{1}, {T}, Sacrifice this artifact: Target creature can't attack or block this turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}"), Costs.Tap, Costs.SacrificeSelf)
        val t = target("target creature", Targets.Creature)
        effect = Effects.CantAttackOrBlock(t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "220"
        artist = "Lindsey Look"
        flavorText = "A weapon best suited for those with good aim and steady hands."
        imageUri = "https://cards.scryfall.io/normal/front/2/5/251f89c5-d4da-4754-83fa-218c8864ef41.jpg"
    }
}
