package com.wingedsheep.mtg.sets.definitions.rna.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Sylvan Brushstrider
 * {2}{G}
 * Creature — Beast
 * 3/2
 *
 * When this creature enters, you gain 2 life.
 */
val SylvanBrushstrider = card("Sylvan Brushstrider") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    oracleText = "When this creature enters, you gain 2 life."
    power = 3
    toughness = 2

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.GainLife(2)
        description = "When this creature enters, you gain 2 life."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "144"
        artist = "Dan Murayama Scott"
        flavorText = "The mournful lowing of brushstriders warns of changing weather and ill winds."
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8bc288a3-ea56-450a-96fd-c2123121f663.jpg?1783933664"
    }
}
