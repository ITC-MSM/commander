package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Knights of Dol Amroth
 * {3}{U}
 * Creature — Human Knight
 * 3/3
 *
 * Whenever you draw your second card each turn, put a +1/+1 counter on this creature.
 */
val KnightsOfDolAmroth = card("Knights of Dol Amroth") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Knight"
    power = 3
    toughness = 3
    oracleText = "Whenever you draw your second card each turn, put a +1/+1 counter on this creature."

    triggeredAbility {
        trigger = Triggers.NthCardDrawn(2)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "59"
        artist = "Eelis Kyttanen"
        flavorText = "And so the companies came and were hailed and cheered and passed through the Gate."
        imageUri = "https://cards.scryfall.io/normal/front/6/6/661338ff-a192-4007-a144-63d00f2e9ecb.jpg?1687277865"
    }
}
