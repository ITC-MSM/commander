package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Devouring Light
 * {1}{W}{W}
 * Instant
 * Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)
 * Exile target attacking or blocking creature.
 */
val DevouringLight = card("Devouring Light") {
    manaCost = "{1}{W}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while casting this spell pays for {1} or one mana of that creature's color.)\nExile target attacking or blocking creature."

    keywords(Keyword.CONVOKE)

    spell {
        val creature = target(
            "target attacking or blocking creature",
            TargetCreature(filter = TargetFilter.AttackingOrBlockingCreature)
        )
        effect = Effects.Exile(creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "13"
        artist = "Pete Venters"
        flavorText = "Into the light the good are welcomed, and with the light the evil are banished."
        imageUri = "https://cards.scryfall.io/normal/front/5/3/53fcc46c-682c-4482-8422-811b951d96cf.jpg?1783943702"

        ruling("2024-01-12", "When calculating a spell's total cost, include any alternative costs, additional costs, or anything else that increases or reduces the cost to cast the spell. Convoke applies after the total cost is calculated. Convoke doesn't change a spell's mana cost or mana value.")
        ruling("2024-01-12", "You can tap any untapped creature you control to convoke a spell, even one you haven't controlled continuously since the beginning of your most recent turn.")
        ruling("2024-01-12", "Tapping an untapped creature that's attacking or blocking to convoke a spell won't cause that creature to stop attacking or blocking.")
        ruling("2024-01-12", "Tapping a multicolored creature using convoke will pay for {1} or one mana of your choice of any of that creature's colors.")
        ruling("2024-01-12", "If a creature you control has a mana ability with {T} in the cost, activating that ability while casting a spell with convoke will result in the creature being tapped before you pay the spell's costs. You won't be able to tap it again for convoke. Similarly, if you sacrifice a creature to activate a mana ability while casting a spell with convoke, that creature won't be on the battlefield when you pay the spell's costs, so you won't be able to tap it for convoke.")
        ruling("2024-01-12", "Because convoke isn't an alternative cost, it can be used in conjunction with alternative costs.")
        ruling("2014-07-18", "The declare blockers step is the last chance to cast Devouring Light before creatures deal their combat damage. However, a creature remains an attacking or blocking creature during the combat damage step and end of combat step. Devouring Light may be cast targeting such a creature during those steps, after the creature has dealt combat damage.")
    }
}
