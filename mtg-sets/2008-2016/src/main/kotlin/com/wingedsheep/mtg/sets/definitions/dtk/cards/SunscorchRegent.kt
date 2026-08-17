package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Sunscorch Regent
 * {3}{W}{W}
 * Creature — Dragon
 * 4/3
 *
 * Flying
 * Whenever an opponent casts a spell, put a +1/+1 counter on this creature and you gain 1 life.
 */
val SunscorchRegent = card("Sunscorch Regent") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dragon"
    oracleText = "Flying\n" +
        "Whenever an opponent casts a spell, put a +1/+1 counter on this creature and you gain 1 life."
    power = 4
    toughness = 3

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.OpponentCastsSpell
        effect = Effects.Composite(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
            Effects.GainLife(1)
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "41"
        artist = "Matt Stewart"
        flavorText = "\"We trust in the scalelords, bringers of justice that none can escape.\"\n—Urdnan, Dromoka warrior"
        imageUri = "https://cards.scryfall.io/normal/front/c/3/c3d2e638-f478-40b8-8c89-120746b7cefd.jpg?1783938611"
    }
}
