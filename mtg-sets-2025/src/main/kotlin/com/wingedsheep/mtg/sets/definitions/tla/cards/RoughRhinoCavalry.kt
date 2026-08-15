package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.firebending
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Rough Rhino Cavalry
 * {4}{R}
 * Creature — Human Mercenary
 * 5/5
 *
 * Firebending 2 (Whenever this creature attacks, add {R}{R}. This mana lasts until end of combat.)
 * Exhaust — {8}: Put two +1/+1 counters on this creature. It gains trample until end of turn.
 *   (Activate each exhaust ability only once.)
 *
 * Firebending 2 is the set keyword combat-mana helper. The exhaust ability composes the two existing
 * atoms — two +1/+1 counters and a trample grant until end of turn — on the source itself.
 */
val RoughRhinoCavalry = card("Rough Rhino Cavalry") {
    manaCost = "{4}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Mercenary"
    power = 5
    toughness = 5
    oracleText = "Firebending 2 (Whenever this creature attacks, add {R}{R}. This mana lasts until end of combat.)\n" +
        "Exhaust — {8}: Put two +1/+1 counters on this creature. It gains trample until end of turn. (Activate each exhaust ability only once.)"

    firebending(2)

    activatedAbility {
        isExhaust = true
        cost = Costs.Mana("{8}")
        effect = Effects.Composite(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self),
            Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "152"
        artist = "Yuhong Ding"
        flavorText = "\"Each one is a different kind of weapons specialist. They are also a very capable singing group.\"\n—Iroh"
        imageUri = "https://cards.scryfall.io/normal/front/a/8/a8bc29cf-f6f8-4bb8-b722-f016b58b6d2d.jpg?1764121046"
    }
}
