package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Krakilin
 * {X}{G}{G}
 * Creature — Beast
 * 0/0
 * This creature enters with X +1/+1 counters on it.
 * {1}{G}: Regenerate this creature.
 */
val Krakilin = card("Krakilin") {
    manaCost = "{X}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 0
    toughness = 0
    oracleText = "This creature enters with X +1/+1 counters on it.\n" +
        "{1}{G}: Regenerate this creature."

    replacementEffect(EntersWithDynamicCounters(count = DynamicAmount.XValue))

    activatedAbility {
        cost = Costs.Mana("{1}{G}")
        effect = RegenerateEffect(EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "235"
        artist = "Richard Kane Ferguson"
        flavorText = "\"A perfect reflection of its world: brutal, fetal, and lacking truth.\"\n" +
            "—Oracle *en*-Vec"
        imageUri = "https://cards.scryfall.io/normal/front/a/9/a90442e8-9d22-4767-9e08-bd314169ea70.jpg"
    }
}
