package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Hour of Reckoning
 * {4}{W}{W}{W}
 * Sorcery
 * Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)
 * Destroy all nontoken creatures.
 */
val HourOfReckoning = card("Hour of Reckoning") {
    manaCost = "{4}{W}{W}{W}"
    colorIdentity = "W"
    typeLine = "Sorcery"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\nDestroy all nontoken creatures."

    keywords(Keyword.CONVOKE)

    spell {
        effect = Effects.DestroyAll(GameObjectFilter.Creature.nontoken())
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "21"
        artist = "Randy Gallegos"
        flavorText = "\"Ravnica, like a hedge, must be pruned, leaving only leaves of verdant uniformity.\" —Niszka, Selesnya evangel"
        imageUri = "https://cards.scryfall.io/normal/front/b/e/bec7a987-1ef2-40aa-a744-92d90b246df4.jpg?1783943699"

        ruling("2024-01-12", "You can tap any untapped creature you control to convoke a spell, even one you haven't controlled continuously since the beginning of your most recent turn.")
        ruling("2024-01-12", "Tapping an untapped creature that's attacking or blocking to convoke a spell won't cause that creature to stop attacking or blocking.")
        ruling("2024-01-12", "When calculating a spell's total cost, include any alternative costs, additional costs, or anything else that increases or reduces the cost to cast the spell. Convoke applies after the total cost is calculated. Convoke doesn't change a spell's mana cost or mana value.")
        ruling("2024-01-12", "If a creature you control has a mana ability with {T} in the cost, activating that ability while casting a spell with convoke will result in the creature being tapped before you pay the spell's costs. You won't be able to tap it again for convoke. Similarly, if you sacrifice a creature to activate a mana ability while casting a spell with convoke, that creature won't be on the battlefield when you pay the spell's costs, so you won't be able to tap it for convoke.")
        ruling("2024-01-12", "Because convoke isn't an alternative cost, it can be used in conjunction with alternative costs.")
        ruling("2024-01-12", "Tapping a multicolored creature using convoke will pay for {1} or one mana of your choice of any of that creature's colors.")
    }
}
