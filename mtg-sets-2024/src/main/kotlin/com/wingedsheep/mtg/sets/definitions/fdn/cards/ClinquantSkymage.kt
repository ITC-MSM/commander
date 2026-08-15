package com.wingedsheep.mtg.sets.definitions.fdn.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Clinquant Skymage
 * {3}{U}
 * Creature — Bird Wizard
 * 1/1
 *
 * Flying
 * Whenever you draw a card, put a +1/+1 counter on this creature.
 *
 * The draw trigger ([Triggers.YouDraw]) fires once per card drawn (CR 121.2), so
 * multi-card draws stack multiple counters.
 */
val ClinquantSkymage = card("Clinquant Skymage") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Bird Wizard"
    power = 1
    toughness = 1
    oracleText = "Flying\nWhenever you draw a card, put a +1/+1 counter on this creature."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouDraw
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "33"
        artist = "Kevin Sidharta"
        imageUri = "https://cards.scryfall.io/normal/front/3/6/36012810-0e83-4640-8ba7-7262229f1b84.jpg?1782689236"
    }
}
