package com.wingedsheep.mtg.sets.definitions.mom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Pile On
 * {3}{B}
 * Instant
 * Convoke
 * Destroy target creature or planeswalker. Surveil 2.
 */
val PileOn = card("Pile On") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Convoke (Your creatures can help cast this spell. Each creature you tap while " +
        "casting this spell pays for {1} or one mana of that creature's color.)\n" +
        "Destroy target creature or planeswalker. Surveil 2. (Look at the top two cards of your " +
        "library, then put any number of them into your graveyard and the rest on top of your " +
        "library in any order.)"

    keywords(Keyword.CONVOKE)

    spell {
        val victim = target("target creature or planeswalker", Targets.CreatureOrPlaneswalker)
        effect = Effects.Destroy(victim) then Patterns.Library.surveil(2)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "122"
        artist = "Javier Charro"
        imageUri = "https://cards.scryfall.io/normal/front/4/9/492e9369-0ca3-4c31-b747-75d615daf6e4.jpg?1783917002"
        ruling(
            "2024-01-12",
            "When calculating a spell's total cost, include any alternative costs, additional " +
                "costs, or anything else that increases or reduces the cost to cast the spell. " +
                "Convoke applies after the total cost is calculated. Convoke doesn't change a " +
                "spell's mana cost or mana value."
        )
        ruling(
            "2024-01-12",
            "You perform the actions stated on a card in sequence. For some spells and abilities, " +
                "you'll surveil last. For others, you'll surveil and then perform other actions."
        )
    }
}
