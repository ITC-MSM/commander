package com.wingedsheep.mtg.sets.definitions.blb.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Flamecache Gecko
 * {1}{R}
 * Creature — Lizard Warlock
 * 2/2
 *
 * When this creature enters, if an opponent lost life this turn, add {B}{R}.
 * {1}{R}, Discard a card: Draw a card.
 */
val FlamecacheGecko = card("Flamecache Gecko") {
    manaCost = "{1}{R}"
    colorIdentity = "BR"
    typeLine = "Creature — Lizard Warlock"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, if an opponent lost life this turn, add {B}{R}.\n{1}{R}, Discard a card: Draw a card."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.OpponentLostLifeThisTurn
        effect = Effects.AddMana(Color.BLACK)
            .then(Effects.AddMana(Color.RED))
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{R}"), Costs.DiscardCard)
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "135"
        artist = "Brian Valeza"
        flavorText = "\"Wood holds a flame for only so long before it crumbles into ash. But stone can burn forever.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fb8e7c97-8393-41b8-bb0b-3983dcc5e7f4.jpg?1721426619"

        ruling("2024-07-26", "Flamecache Gecko's ability cares whether an opponent lost life this turn, not how their life total changed. For example, an opponent who gained 2 life and lost 1 life in the same turn still lost life.")
        ruling("2024-07-26", "Flamecache Gecko's first ability isn't a mana ability. It uses the stack and can be responded to.")
    }
}
