package com.wingedsheep.mtg.sets.definitions.lci.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Geological Appraiser
 * {2}{R}{R}
 * Creature — Human Artificer
 * 3/2
 * When this creature enters, if you cast it, discover 3.
 */
val GeologicalAppraiser = card("Geological Appraiser") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Artificer"
    power = 3
    toughness = 2
    oracleText = "When this creature enters, if you cast it, discover 3."
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.WasCast
        effect = Effects.Discover(3)
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "150"
        artist = "Alix Branwyn"
        imageUri = "https://cards.scryfall.io/normal/front/7/f/7f9c1a82-695b-4df2-8e51-2d71a62e7baf.jpg?1782694489"
    }
}
