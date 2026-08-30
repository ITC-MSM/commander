package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Student of Ojutai
 * {3}{W}
 * Creature — Human Monk
 * 2 / 4
 *
 * Whenever you cast a noncreature spell, you gain 2 life.
 *
 * The Monk shell without prowess: the printed line is a bare [Triggers.YouCastNoncreature] trigger
 * whose effect is life gain, so `prowess()` would be wrong — it would add a +1/+1 trigger the card
 * doesn't have. `Effects.GainLife` defaults to the controller, which is the printed "you".
 */
val StudentOfOjutai = card("Student of Ojutai") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Monk"
    power = 2
    toughness = 4
    oracleText = "Whenever you cast a noncreature spell, you gain 2 life."

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.GainLife(2)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "40"
        artist = "Jason A. Engle"
        flavorText = "\"Human enlightenment is a firefly that sparks in the night. Dragon enlightenment is a beacon that disperses all darkness.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d06616c-a0df-4d2d-8bfc-9f59060d323b.jpg?1783938611"
    }
}
