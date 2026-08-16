package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Colossal Majesty
 * {2}{G}
 * Enchantment
 * At the beginning of your upkeep, if you control a creature with power 4 or greater, draw a card.
 */
val ColossalMajesty = card("Colossal Majesty") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Enchantment"
    oracleText = "At the beginning of your upkeep, if you control a creature with power 4 or greater, draw a card."

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        interveningIf = Conditions.YouControl(GameObjectFilter.Creature.powerAtLeast(4))
        effect = Effects.DrawCards(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "173"
        artist = "Randy Vargas"
        flavorText = "\"Might doesn't just build empires. It protects them.\" —Inti, Sun Empire knight"
        imageUri = "https://cards.scryfall.io/normal/front/9/3/93f99796-ddc5-4ccd-b925-35622a8648b8.jpg?1783934541"
        ruling(
            "2018-07-13",
            "If you don't control a creature with power 4 or greater as your upkeep begins, " +
                "Colossal Majesty's ability won't trigger. You can't take any actions during your " +
                "turn before your upkeep begins."
        )
        ruling(
            "2018-07-13",
            "If you don't control a creature with power 4 or greater as Colossal Majesty's ability " +
                "resolves, you won't draw a card."
        )
        ruling(
            "2018-07-13",
            "The creature with power 4 or greater that you control as Colossal Majesty's ability " +
                "resolves doesn't have to be the same creature with power 4 or greater that was " +
                "under your control as the ability triggered."
        )
        ruling(
            "2018-07-13",
            "You draw only one card, no matter how many creatures with power 4 or greater you control."
        )
    }
}
