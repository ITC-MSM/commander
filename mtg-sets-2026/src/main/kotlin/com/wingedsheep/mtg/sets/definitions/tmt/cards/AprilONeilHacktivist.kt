package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * April O'Neil, Hacktivist
 * {3}{U}
 * Legendary Creature — Human Scientist
 * 1/5
 *
 * At the beginning of your end step, draw a card for each card type among spells
 * you've cast this turn.
 */
val AprilONeilHacktivist = card("April O'Neil, Hacktivist") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Scientist"
    oracleText = "At the beginning of your end step, draw a card for each card type among spells you've cast this turn."
    power = 1
    toughness = 5

    triggeredAbility {
        trigger = Triggers.YourEndStep
        effect = Effects.DrawCards(
            DynamicAmount.SpellsCastThisTurn(Player.You, countDistinctCardTypes = true)
        )
        description = "At the beginning of your end step, draw a card for each card type among spells you've cast this turn."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "29"
        artist = "Xabi Gaztelua"
        flavorText = "In the fight between mutants, ninjas, and aliens, don't underestimate a resourceful human."
        imageUri = "https://cards.scryfall.io/normal/front/f/0/f0147f6d-b797-4d5e-aca2-a9d309896eca.jpg?1760102651"
    }
}
