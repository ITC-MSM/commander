package com.wingedsheep.mtg.sets.definitions.arb.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Jenara, Asura of War
 * {G}{W}{U}
 * Legendary Creature — Angel
 * 3/3
 *
 * Flying
 * {1}{W}: Put a +1/+1 counter on Jenara.
 */
val JenaraAsuraOfWar = card("Jenara, Asura of War") {
    manaCost = "{G}{W}{U}"
    colorIdentity = "WUG"
    typeLine = "Legendary Creature — Angel"
    power = 3
    toughness = 3
    oracleText = "Flying\n{1}{W}: Put a +1/+1 counter on Jenara."

    keywords(Keyword.FLYING)

    activatedAbility {
        cost = Costs.Mana("{1}{W}")
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "128"
        artist = "Chris Rahn"
        flavorText = "Wounded soldiers looked up, grateful for her appearance. But she passed over them, her eyes firmly on their foe."
        imageUri = "https://cards.scryfall.io/normal/front/d/6/d643c335-e3a7-461d-a024-095795ab6770.jpg"
    }
}
