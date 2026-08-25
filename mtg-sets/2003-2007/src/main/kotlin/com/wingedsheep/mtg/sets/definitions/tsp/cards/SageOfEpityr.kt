package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sage of Epityr
 * {U}
 * Creature — Human Wizard
 * 1/1
 * When this creature enters, look at the top four cards of your library, then put them back in any
 * order.
 */
val SageOfEpityr = card("Sage of Epityr") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 1
    toughness = 1
    oracleText = "When this creature enters, look at the top four cards of your library, then put them back in any order."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Patterns.Library.lookAtTopAndReorder(4)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "74"
        artist = "Randy Gallegos"
        flavorText = "Clairvoyants across Dominaria were driven mad by the overload from the widening time rifts, while other random folk gained the gift of future sight."
        imageUri = "https://cards.scryfall.io/normal/front/8/e/8e2ea578-069e-4020-a762-d108a3e14861.jpg"
    }
}
