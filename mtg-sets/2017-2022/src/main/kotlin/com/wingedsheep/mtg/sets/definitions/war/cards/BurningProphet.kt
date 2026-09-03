package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Burning Prophet
 * {1}{R}
 * Creature — Human Wizard
 * 1/3
 * Whenever you cast a noncreature spell, this creature gets +1/+0 until end of turn, then scry 1.
 */
val BurningProphet = card("Burning Prophet") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Wizard"
    oracleText = "Whenever you cast a noncreature spell, this creature gets +1/+0 until end of turn, then scry 1."
    power = 1
    toughness = 3

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = Effects.Composite(listOf(
            Effects.ModifyStats(1, 0, EffectTarget.Self),
            Effects.Scry(1)
        ))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "117"
        artist = "Mathias Kollros"
        flavorText = "\"This day will end in death, but not the one you hoped for. This day will end in victory, but not the one you expected.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/1/01c5b095-13c9-4673-bf0c-553de455e521.jpg"
    }
}
