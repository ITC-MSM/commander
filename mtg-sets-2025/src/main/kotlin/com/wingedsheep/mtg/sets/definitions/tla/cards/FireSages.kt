package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.firebending
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Fire Sages — {1}{R}
 * Creature — Human Cleric
 * 2/2
 * Firebending 1 (Whenever this creature attacks, add {R}. This mana lasts until end of combat.)
 * {1}{R}{R}: Put a +1/+1 counter on this creature.
 */
val FireSages = card("Fire Sages") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Cleric"
    power = 2
    toughness = 2
    oracleText = "Firebending 1 (Whenever this creature attacks, add {R}. This mana lasts until end of combat.)\n" +
        "{1}{R}{R}: Put a +1/+1 counter on this creature."

    firebending(1)

    activatedAbility {
        cost = Costs.Mana("{1}{R}{R}")
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "136"
        artist = "Yuu Fujiki"
        flavorText = "\"Things have changed. In the past, the sages were loyal only to the Avatar.\"\n—Fire Sage Shyu"
        imageUri = "https://cards.scryfall.io/normal/front/7/1/71a9fa44-306f-417a-a9d9-991aff95025c.jpg?1764120933"
    }
}
