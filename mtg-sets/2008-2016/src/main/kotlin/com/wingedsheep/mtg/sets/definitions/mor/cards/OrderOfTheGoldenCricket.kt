package com.wingedsheep.mtg.sets.definitions.mor.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.OptionalCostEffect
import com.wingedsheep.sdk.scripting.effects.PayManaCostEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Order of the Golden Cricket
 * {1}{W}
 * Creature — Kithkin Knight
 * 2/2
 * Whenever this creature attacks, you may pay {W}. If you do, it gains flying until end of turn.
 *
 * The "you may pay {W}" gate is an [OptionalCostEffect] (same shape as Descendant of Storms);
 * "it" is the attacker itself, so the granted keyword lands on [EffectTarget.Self].
 */
val OrderOfTheGoldenCricket = card("Order of the Golden Cricket") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Kithkin Knight"
    power = 2
    toughness = 2
    oracleText = "Whenever this creature attacks, you may pay {W}. If you do, it gains flying until end of turn."

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = OptionalCostEffect(
            cost = PayManaCostEffect(ManaCost.parse("{W}")),
            ifPaid = Effects.GrantKeyword(Keyword.FLYING, EffectTarget.Self),
        )
        description = "Whenever this creature attacks, you may pay {W}. If you do, it gains flying until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "19"
        artist = "Mark Zug"
        flavorText = "\"Should you take it in mind to ride a springjack, remember: there are easier ways to fly, and harder ways to break your skull.\" —Lann of Cloverdell"
        imageUri = "https://cards.scryfall.io/normal/front/5/d/5d1c2f16-5661-4c17-8265-f4b88ff1e833.jpg?1783942804"
    }
}
