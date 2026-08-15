package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Rebellious Captives
 * {1}{G}
 * Creature — Human Peasant Ally
 * 2/2
 *
 * Exhaust — {6}: Put two +1/+1 counters on this creature, then earthbend 2.
 *   (Activate each exhaust ability only once.)
 *
 * The exhaust ability composes two +1/+1 counters on the source with [Effects.Earthbend] (which
 * animates a target land you control into a counter-bearing creature-land). The land target is
 * declared in the ability and threaded into the earthbend.
 */
val RebelliousCaptives = card("Rebellious Captives") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Peasant Ally"
    power = 2
    toughness = 2
    oracleText = "Exhaust — {6}: Put two +1/+1 counters on this creature, then earthbend 2. (Target land you control becomes a 0/0 creature with haste that's still a land. Put two +1/+1 counters on it. When it dies or is exiled, return it to the battlefield tapped. Activate each exhaust ability only once.)"

    activatedAbility {
        isExhaust = true
        cost = Costs.Mana("{6}")
        val land = target("target land you control", TargetObject(filter = TargetFilter.Land.youControl()))
        effect = Effects.Composite(
            Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self),
            Effects.Earthbend(2, land),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "191"
        artist = "Ittoku"
        flavorText = "\"The Fire Nation will regret the day they set foot on our land!\""
        imageUri = "https://cards.scryfall.io/normal/front/e/6/e64a4a8b-323c-4c8b-92af-1a3295799e65.jpg?1764121300"
    }
}
