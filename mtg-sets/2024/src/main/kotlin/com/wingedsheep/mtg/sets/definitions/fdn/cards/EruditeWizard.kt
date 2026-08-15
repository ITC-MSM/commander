package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Erudite Wizard
 * {2}{U}
 * Creature — Human Wizard
 * 2/3
 *
 * Whenever you draw your second card each turn, put a +1/+1 counter on this creature.
 */
val EruditeWizard = card("Erudite Wizard") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 3
    oracleText = "Whenever you draw your second card each turn, put a +1/+1 counter on this creature."

    triggeredAbility {
        trigger = Triggers.NthCardDrawn(2)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
        description = "Whenever you draw your second card each turn, put a +1/+1 counter on this creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "37"
        artist = "Ioannis Fiore"
        flavorText = "\"Pretty baubles and gilded palaces may appeal to the egotist, but true wealth comes only through knowledge.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/2/9273c417-0fcd-4273-b24e-afff76336d0c.jpg?1782689234"
    }
}
