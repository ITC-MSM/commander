package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Omni-Cheese Pizza
 * {2}
 * Artifact — Food
 *
 * When this artifact enters, draw a card.
 * {1}, {T}, Sacrifice this artifact: Add one mana of any color.
 * {2}, {T}, Sacrifice this artifact: You gain 3 life.
 */
val OmniCheesePizza = card("Omni-Cheese Pizza") {
    manaCost = "{2}"
    typeLine = "Artifact — Food"
    oracleText = "When this artifact enters, draw a card.\n{1}, {T}, Sacrifice this artifact: Add one mana of any color.\n{2}, {T}, Sacrifice this artifact: You gain 3 life."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(1)
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{1}"),
            Costs.Tap,
            Costs.SacrificeSelf
        )
        effect = Effects.AddAnyColorMana()
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(
            Costs.Mana("{2}"),
            Costs.Tap,
            Costs.SacrificeSelf
        )
        effect = Effects.GainLife(3)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "176"
        artist = "Gabriel Tanko"
        flavorText = "\"And just a sprig of parsley for color . . .\"\n—Michelangelo"
        imageUri = "https://cards.scryfall.io/normal/front/f/2/f2c8397c-2014-4a43-9ed7-41795880011f.jpg?1771587071"
    }
}
